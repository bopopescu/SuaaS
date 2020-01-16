import base
from google.appengine.ext import vendor
from Validation.SuAASValidations import Validation
from Model.Summarizer import FrequencySummarizer
from Model.DataStoreManager import ResultsHandler

from Model import BotoSqS

vendor.add('lib')
import simplejson as json


class SuAASRequest(base.BaseHandler):
    def post(self):
        valid = Validation(self.inputBody)
        # First validate the request
        valid = valid.validate()
        if bool(valid.get("error")):
            return self.respond(400, valid)
        if self.inputBody['type'] == 'text':
            fs = FrequencySummarizer()
            result = fs.summarize(self.inputBody['text'], self.inputBody['numberOfSentences'])
            resultHandler = ResultsHandler()

            return self.respond(200, {
                "result": result,
                "status": "Completed",
                "key": resultHandler.createFinalResult(result, self.inputBody['text'],
                                                       self.inputBody['numberOfSentences'], self.inputBody['type'])
            })
        else:
            resultHandler = ResultsHandler()
            key = resultHandler.createQueueResults(self.inputBody['link'], self.inputBody['numberOfSentences'],
                                                   self.inputBody['type'], self.inputBody['token'],
                                                   self.inputBody['tokenType'])

            BotoSqS.sendMessage(json.dumps({
                'link': self.inputBody['link'],
                'key': key,
                'type': self.inputBody['type'],
                'token': self.inputBody['token'],
                'tokenType': self.inputBody['tokenType']
            }))
            self.respond(200, {
                "status": "Queued",
                "key": key
            })

    def get(self):
        resultHandler = ResultsHandler()
        print(self.request.get('key'))
        result = resultHandler.getResult(self.request.get('key'))
        if result == None:
            return self.respond(400, {
                "message": "invalid id"
            })
        else:

            fs = FrequencySummarizer()
            self.respond(200, {
                "status": result.status,
                "result": fs.summarize(result.text, result.numberOfSentences),
                'key': int(self.request.get('key'))
            })
