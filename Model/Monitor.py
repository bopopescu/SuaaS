#Mointor is implemented on the GCE which is a PaaS (Server side)
import time
import BotoSqS
import threading
from google.cloud import datastore
import json
import os
from Summarizer import FrequencySummarizer

# os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "./credentials.json"
#firebase cloud messaging
from pyfcm import FCMNotification

api_key = "Your API KEY FoR Firebase App"

push_service = FCMNotification(api_key)

#for firebase
message_title = "SuAAS Update"
message_body = "Hi, your summary is ready."

MAX_INSTANCES = 4
instances_name = ['instance-6', 'instance-7', 'instance-8', 'instance-9']
instance_status = ['inactive', 'inactive', 'inactive', 'inactive']


def monitor():
    ######################################
    # Once the result are ready send the
    # notifications.
    ######################################
    while True:
		#One worker/server per request and Max = 4
        requiredServers = int(BotoSqS.getRequestQueueCount("requestQueue")) - 1
        print(str(requiredServers) + ' server required')
        if int(requiredServers) > 0:
            requiredServers = min(MAX_INSTANCES, requiredServers)
            i = 0

            while i < int(MAX_INSTANCES) and requiredServers != 0:
                result = os.popen("gcloud compute instances describe --zone=us-west1-c " + instances_name[
                    i] + "|grep 'status:'").read()

                print(result)
                if 'RUNNING' not in result:
                    os.system('gcloud compute instances start --zone=us-west1-c ' + instances_name[i])
                    requiredServers = requiredServers - 1

                i = i + 1


        time.sleep(45)



def textWorker(url, noOfSentences):
    import os
    os.system('wget -O  /home/skapoo14/workspace/CloudComputing/CloudProject2/Model/tempImage ' + url)
    os.system("textract /home/skapoo14/workspace/CloudComputing/CloudProject2/Model/tempImage > tempImage.txt")
    os.system('rm -rf /home/skapoo14/workspace/CloudComputing/CloudProject2/Model/tempImage ')
    fs = FrequencySummarizer()
    string = open('tempImage.txt').read().replace('\n', '')
    data = open('tempImage.txt').read().decode('utf-8').replace('\n', '')
    os.system('rm -rf tempImage.txt')
    return data


def ocrWorker(url, noOfSentences):
    import os

    os.system('wget -O  /home/skapoo14/workspace/CloudComputing/CloudProject2/Model/tempImage.png ' + url)
    time.sleep()
    os.system("textract /home/skapoo14/workspace/CloudComputing/CloudProject2/Model/tempImage.png > tempImage.txt")
    os.system('rm -rf /home/skapoo14/workspace/CloudComputing/CloudProject2/Model/tempImage.png')

    fs = FrequencySummarizer()
    string = open('tempImage.txt').read().replace('\n', '')
    data = open('tempImage.txt').read().decode('utf-8').replace('\n', '')
    os.system('rm -rf tempImage.txt')
    return data


def worker():
    i = 0
    while True:
		#If there are requests on the queue, get 1 message
        if int(BotoSqS.getRequestQueueCount('requestQueue')) != 0:
            messages = BotoSqS.getMessage(1, "requestQueue")
            if len(messages) != 0:
                errorOccured = False
                for message in messages:
					#get a datastore object
                    datastore_client = datastore.Client()
					#get the message body (url)
                    dataDict = json.loads(message.get_body())
                    key = dataDict['key']
                    url = dataDict['link']
                    query = datastore_client.query(kind='Results')
                    first_key = datastore_client.key('Results', int(key))
                    query.key_filter(first_key, '=')
                    entities = list(query.fetch())
                    if len(entities) != 0:
                        print((entities[0]).get('numberOfSentences'))
                        try:
                            if dataDict['type'] == 'ocr':
                                result = ocrWorker(url, entities[0].get('numberOfSentences'))
                                print(key)
                                entities[0].update({
                                    'text': result,
                                    'status': 'Completed'
                                })
                                print(entities[0])
                                datastore_client.put(entities[0])
                            elif dataDict['type'] == 'webArticle':
                                result = textWorker(url, entities[0].get('numberOfSentences'))
                                print(key)
                                entities[0].update({
                                    'text': result,
                                    'status': 'Completed'
                                })
                                print(entities[0])
                                datastore_client.put(entities[0])
                            elif dataDict['type'] == 'audioLink':
                                print(url)
                                result = audioToText(url)
                                print(key)
                                print(result)
                                entities[0].update({
                                    'text': result,
                                    'status': 'Completed'
                                })
                                print(entities[0])
                                datastore_client.put(entities[0])
                            data_message = {
                                'key': dataDict['key']
                            }
                            result = push_service.notify_single_device(registration_id=dataDict['token'],
                                                                       message_title=message_title,
                                                                       message_body=message_body,
                                                                       data_message=data_message)
                            print(dataDict['token'])
                            print(result)
                        except Exception as e:
                            print(e)
                            print('something went wrong')
                            data_message = {
                                'key': dataDict['key']
                            }
                            result = push_service.notify_single_device(registration_id=dataDict['token'],
                                                                       message_title=message_title,
                                                                       message_body=message_body,
                                                                       data_message=data_message)

                    BotoSqS.deleteMessage(messages)
                    time.sleep(0.5)
        else:
            if i == 1:
                os.system('sudo shutdown -h now')
            i = i + 1


def checkServers():
    if BotoSqS.getRequestQueueCount("responseQueue") != 0:
        messages = BotoSqS.getMessage(1, 'responseQueue')
    return None

#Just convert audio to text using "gcloud ml speech recognize-long-running"
def audioToText(fileURL):
    import uuid
    tempFile = uuid.uuid4()
    downloadCommand = 'wget -O audioFileTest ' + fileURL
    os.system(downloadCommand)

    os.system('ffmpeg -i audioFileTest -sample_rate 22100 -ac 1 ' + str(tempFile) + '.flac')
    os.system('gsutil cp ' + str(tempFile) + '.flac gs://audio_storage_cse546_1/')
    os.system(
        'gcloud ml speech recognize-long-running gs://audio_storage_cse546_1/' + str(
            tempFile) + '.flac  --language-code=en-US --encoding=FLAC > audioFileMp3.txt')
    os.system('gsutil rm gs://audio_storage_cse546_1/' + str(tempFile) + '.flac')
    file = open("audioFileMp3.txt", "r")
    jsonText = file.read()
    pyJSON = json.loads(jsonText)
    audioText = ''
    for eachResult in pyJSON['results']:
        for eachLine in eachResult['alternatives']:
            audioText += eachLine['transcript']
            audioText += '. '
    os.system('rm -rf audioFileTest')
    os.system('rm -rf audioFileMp3.txt')
    os.system('rm -rf ' + str(tempFile) + '.flac')
    return audioText


temp = threading.Thread(target=worker)
temp.start()
temp1 = threading.Thread(target=monitor)
temp1.start()
