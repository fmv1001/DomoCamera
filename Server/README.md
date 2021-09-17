# Servidor
En esta carpeta podemos encontrar los archivos necesarios para el funcionamiento del servidor.
# Estructura de archivos del servidor
Para la estructura del servidor se ha utilizado el patrón de modelo-vista-controlador (MVC). 
El patrón MVC es un patrón arquitectónico que separa el sistema en tres partes, los datos, la lógica de la aplicación y la interfaz de usuario. El modelo es la capa de datos, la vista es la interfaz de usuario y el controlador, la lógica de la aplicación.

En el servidor se ha aplicado este patrón de la siguiente manera:

  - `data`: Una primera carpeta de datos que contiene la base de datos y el ORM (schema.py) para el acceso a la misma, todo ello se encuentra dentro de la carpeta `db`.
  - `logic`: Una carpeta llamada `logic` que contiene la lógica de negocio del servidor. Dentro de ella encontramos el archivo `MainServer.py`.
  - `presentation`: Una última carpeta que implementa la capa de presentación (presentation). Dentro de ella encontramos el archivo `CamConnex.py`, que implementa el envío de imágenes a los clientes.
