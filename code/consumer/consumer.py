import pika, logging, sys, argparse, time
from argparse import RawTextHelpFormatter
from time import sleep
from prometheus_client import start_http_server,Gauge


global counter
counter = 0

def on_message(channel, method_frame, header_frame, body):
    print (method_frame.delivery_tag)
    print (body)
    print
    LOG.info('Message has been received %s', body)
    counter+=1
    consumer_messages_count.set(counter)

    channel.basic_ack(delivery_tag=method_frame.delivery_tag)

def prometheus_register():
    """ Register the metrics for Prometheus.
    Args : None
    Return : None
    """
    global consumer_messages_count
    consumer_messages_count = Gauge('consumer_messages_count', 'Consumer Messages count')


if __name__ == '__main__':
    examples = sys.argv[0] + " -p 5672 -s rabbitmq "
    parser = argparse.ArgumentParser(formatter_class=RawTextHelpFormatter,
                                     description='Run consumer.py',
                                     epilog=examples)
    parser.add_argument('-p', '--port', action='store', dest='port', help='The port to listen on.')
    parser.add_argument('-s', '--server', action='store', dest='server', help='The RabbitMQ server.')

    args = parser.parse_args()
    if args.port == None:
        print ("Missing required argument: -p/--port")
        sys.exit(1)
    if args.server == None:
        print ("Missing required argument: -s/--server")
        sys.exit(1)

    # sleep a few seconds to allow RabbitMQ server to come up
    sleep(5)
    logging.basicConfig(level=logging.INFO)
    LOG = logging.getLogger(__name__)
    credentials = pika.PlainCredentials('guest', 'guest')
    parameters = pika.ConnectionParameters(args.server,
                                           int(args.port),
                                           '/',
                                           credentials)
    connection = pika.BlockingConnection(parameters)

    print('Starting Prometheus web-server')
    start_http_server(9422)
    print('Registering metrics to Prometheus')
    prometheus_register()
    channel = connection.channel()


    channel.queue_declare('pc')
    channel.basic_consume(on_message, 'pc')

    try:
        channel.start_consuming()
    except KeyboardInterrupt:
        channel.stop_consuming()
    connection.close()
