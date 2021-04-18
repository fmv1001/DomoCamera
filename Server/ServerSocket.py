""" Modulo de la clase servidor
"""

import socket
import sys

class SocketServer():
    """ Clase responsable del funcionamiento del servidor
    """
    

    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind the socket to the port
    server_address = ('', 9999)
    print('starting up on {} port {}'.format(*server_address))
    sock.bind(server_address)

    # Listen for incoming connections
    sock.listen(1)

    while True:
        # Wait for a connection
        print('waiting for a connection')
        connection, client_address = sock.accept()
        try:
            print('connection from', client_address)

            # Receive the data in small chunks and retransmit it
            while True:
                data = connection.recv(512)
                print(str(data.decode("UTF8"))," es la respuesta")
                if data:
                    print('sending data back to the client')
                    connection.send(data)
                else:
                    print('no data from', client_address)
                    break

        finally:
            # Clean up the connection
            connection.close()

SocketServer()