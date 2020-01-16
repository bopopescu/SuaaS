# SuaaS

Summarization as a Service

Summary as a Service (SuaaS) application will provide the summary of any media that can be represented in the form of text. The application currently supports 3 types of media: Audio, Text and Text Images. For example, you got any lecture, and you want a summary of that lecture, feed the application with the audio and you get the text summary of that lecture. An application to reduce your reading effort and stay up to date with all the things around you. The front end of the application is an android (and Web) application and supported by Google and Amazon cloud technologies on the back end.

Design:

The client will send text or the link to Text Image or Audio file to the our application backend implemented using Google App Engine. In case of a text, the GAE instance can directly ready the text for summarization. For text image android first converts the image into text using vision service provided by google and then the text is sent to the backend. In case of Audio files, the link of the file from Google Firebase is pushed onto the Amazon SQS queue along with the "Token ID" (Unique ID assigned to clients). Google Compute Engine (GCE) is used to implement the monitor, which keeps polling the SQS queue for requests. SQS was used in place of Google Task Queue because the Task Queue API gor GCE was deprecated and the new version of the API is still in the Alpha testing phase.

GCE automatically scales up the number of worker nodes to process the requests based on the number of requests remaining on the SQS queue. Once the GCE instance receives a request for summarization, it converts the Image text or Audio files to text and then uses an algorithm to summarize the text. Then the GCE instance stores the response in the google datastore parallely issuing a request for the Firebase Cloud Messaging service to send a notification to the respective client indicating the response is ready. Once the response is stored on the Datastore , App Engine picks up the response and sends it back to the client.

Code:

monitor.py
The monitor does the following tasks:
1. Performs Auto Scaling by polling the SQS request queue to get the count of the number of requests.
2. Converts the data to be summarized into text format.
In case of audio, monitor uses Google Speech Recognition API to convert audio to text format. In case of a textual Image, textract is used to extract textual information.
3. Spawns worker nodes to summarize the textual data.
4. Store the summarized responses in the Google Datastore.
5. Send notification to devices using Firebase Cloud Messaging.

DataStoreManager.py
DataStoreManager.py uses the Google Datastore Client Library NDB to allow Python apps implemented on the Google App Engine to connect with Google Datastore. In DataStoreManager we use the NDB library to query the Datastore to perform operations to store and fetch data from the datastore.

Summarizer.py
Summarizer,py contains code used to summarize textual data. Summarizer first removes the basic stopwords present in english language. Summarizer uses the Natural Language Tool Kit libraries sent_tokenize and word_tokenize to convert sentences and words into vectors. Then the summarizer calculates the frequncies of the words among the sentences using the word and sentence vectors. It then ranks the sentences based on their occuring frequncies. The summarizer finally returns the top "n" sentences, where n is the number of sentences the user has requested.
The application is highly flexible and decoupled which allows us to replace the summarizer model when a new and improved model is available.

SuAASValidations.py
SuAASValidations is responsible for checking the proper format of requests, responses and check what type of the data source (Text/ocr/Web Article/Audio Link). The Validator sends appropriate messages to the parties involved in case of an invalid message format.

SuaaSRequest.py
SuaaSRequest performs POST and GET protocols to communicate with the servers implemented on GCE. SuaaSRequest also checks the type of data source and if the data source is text, it directly calls the summarizer FrequencySummarizer() to summarize the data. If the data to be summarized is either in Audio ot Image format, it packs the request data into a JSON format and places it onto the SQS request queue. It uses get protocol to get the data from the server.
