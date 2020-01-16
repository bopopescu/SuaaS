aws_secret_access_key = "your aws secret key here"
aws_access_key_id = "your aws access key here"


def sendMessage(data=None):
    print('here')
    import boto.sqs

    client = boto.sqs.connect_to_region('us-west-1', aws_access_key_id=aws_access_key_id,
                                        aws_secret_access_key=aws_secret_access_key)

    requestQueue = client.get_queue("requestQueue")
    print(client.send_message(requestQueue, data, delay_seconds=None))


def getMessage(number_of_messages=1, queue_name="responseQueue"):
    import boto.sqs

    client = boto.sqs.connect_to_region('us-west-1', aws_access_key_id=aws_access_key_id,
                                        aws_secret_access_key=aws_secret_access_key)
    responseQueue = client.get_queue(queue_name)
    messages = responseQueue.get_messages(num_messages=number_of_messages)

    return messages


def deleteMessage(messages, queue_name="requestQueue"):
    import boto.sqs

    client = boto.sqs.connect_to_region('us-west-1', aws_access_key_id=aws_access_key_id,
                                        aws_secret_access_key=aws_secret_access_key)
    responseQueue = client.get_queue(queue_name)
    client.delete_message_batch(responseQueue, messages)


def getRequestQueueCount(queueName="requestQueue"):
    import boto.sqs

    client = boto.sqs.connect_to_region('us-west-1', aws_access_key_id=aws_access_key_id,
                                        aws_secret_access_key=aws_secret_access_key)
    queue = client.get_queue(queueName)
    result = queue.get_attributes(attributes='ApproximateNumberOfMessages')
    return result['ApproximateNumberOfMessages']
