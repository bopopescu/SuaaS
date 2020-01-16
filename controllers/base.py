import webapp2
from google.appengine.ext import vendor

vendor.add('lib')
import simplejson as json

CONTENT_TYPE_HEADER = 'Content-Type'
JSON_CONTENT_TYPE = 'application/json'


class BaseHandler(webapp2.RequestHandler):
    def __init__(self, request=None, response=None, requires_authentication=False):
        super(BaseHandler, self).__init__(request, response)
        self.setupResponse()
        self.inputBody = dict()

        if (self.request.body):
            print(self.request.body)
            self.inputBody = json.loads(self.request.body)

    def setupResponse(self):
        self.request.path_info_pop()
        self.headers = self.request.headers
        self.response.status = 404
        self.response.headers[CONTENT_TYPE_HEADER] = JSON_CONTENT_TYPE

    def respond(self, status_code=404, body=None):
        self.response.status = status_code
        self.response.write(json.dumps(body)
                            if body is not None else '{}')
