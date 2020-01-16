from google.appengine.ext import ndb


class Results(ndb.Model):
    created = ndb.DateTimeProperty(auto_now_add=True)
    status = ndb.StringProperty(required=True, choices=["Completed", "In-Progress", "Queued"])
    result = ndb.JsonProperty()
    text = ndb.TextProperty()
    link = ndb.StringProperty()
    numberOfSentences = ndb.IntegerProperty()
    type = ndb.StringProperty()
    token = ndb.StringProperty()


class ResultsHandler():
    def createFinalResult(self, answer, text, count, type):
        result = Results()
        result.result = answer
        result.status = 'Completed'
        result.text = text
        result.type = type
        result.numberOfSentences = count
        result.put()
        return result.key.id()

    def createQueueResults(self, link, count, type, token, tokenType):
        result = Results()
        result.link = link
        result.text = ''
        result.numberOfSentences = count
        result.result = ['Not processed yet...']
        result.status = "Queued"
        result.token = token
        result.tokenType = tokenType
        result.type = type
        result.put()
        return result.key.id()

    def getResult(self, key):
        result = Results.get_by_id(int(key))
        return result
