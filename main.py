#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import webapp2
import json
from controllers.SuAASRequest import SuAASRequest

from google.appengine.ext import vendor

# Add any libraries install in the "lib" folder.
vendor.add('lib')


class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/html'
        object = {
            'message': 'This is a Summary as Service API!!!'
        }
        self.response.out.write(open('./www/index.html').read())


class JavaScript(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/javascript'
        object = {
            'message': 'This is a Summary as Service API!!!'
        }
        self.response.out.write(open('./www/js/index.js').read())


class FireBase(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/javascript'
        object = {
            'message': 'This is a Summary as Service API!!!'
        }
        self.response.out.write(open('./www/js/firebase-messaging-sw.js').read())


class Image(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'image/png'
        object = {
            'message': 'This is a Summary as Service API!!!'
        }
        self.response.out.write(open('./www/images/logo.png').read())


ROUTES = [
    ('/', MainHandler),
    ('/api/suaas', SuAASRequest),
    ('/index.js', JavaScript),
    ('/firebase-messaging-sw.js', FireBase),
    ('/logo.png', Image)
]

app = webapp2.WSGIApplication(ROUTES, debug=True)
