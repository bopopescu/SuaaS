import os

if os.getenv('SERVER_SOFTWARE', '!Dev').startswith('Dev'):
    subdomain = '0.0.0.0:8080'
else:
    subdomain = '0.0.0.0:8080'
