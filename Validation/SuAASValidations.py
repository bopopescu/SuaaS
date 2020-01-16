import re

regex = re.compile(
    r'^(?:http|ftp)s?://'  # http:// or https://
    r'(?:(?:[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?\.)+(?:[A-Z]{2,6}\.?|[A-Z0-9-]{2,}\.?)|'  # domain...
    r'localhost|'  # localhost...
    r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})'  # ...or ip
    r'(?::\d+)?'  # optional port
    r'(?:/?|[/?]\S+)$', re.IGNORECASE)


class Validation:
    def __init__(self, body):
        self._body = body

    def validate(self):
        ###########################################
        # What type of request is there validation
        ###########################################
        if "type" not in self._body.keys():
            return {
                "error": True,
                "Message": "Request does not contain the type key"
            }
        elif self._body.get('type') not in ['text', 'ocr', 'webArticle', 'audioLink']:
            return {
                'error': True,
                "Message": "Type attribute should be from text or ocr values"
            }

        ###########################################
        # Request has all the information
        ###########################################
        if self._body.get('type') == 'text':
            if "text" not in self._body:
                return {
                    "error": True,
                    "Message": "Request does not have the text"
                }
        if self._body.get('type') in ['ocr', 'webArticle', 'audioLink']:
            if 'link' not in self._body:
                return {
                    "error": True,
                    "Message": "Request does not have the link"
                }
            elif re.match(regex, self._body['link']) is None:
                return {
                    'error': True,
                    'Message': 'link not valid url'
                }
        ###########################################
        # Push notification Data
        ###########################################
        if 'tokenType' not in self._body:
            return {
                'error': True,
                'Message': 'tokenType not provided'
            }
        elif self._body.get("tokenType") not in ['fcm']:
            return {
                'error': True,
                'Message': 'Currently on fcm is supported'
            }
        if "token" not in self._body:
            return {
                'error': True,
                'Message': 'Push notification token is not provided'
            }
        elif len(self._body['token']) == 0:
            return {
                'error': True,
                'Message': 'token is empty'
            }
        return {
            'error': False,
            'Message': 'All ok'
        }
