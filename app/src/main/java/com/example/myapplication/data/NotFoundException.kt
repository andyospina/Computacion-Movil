package com.example.myapplication.data

/** El recurso pedido no existe en el backend (documento de Firestore inexistente, por ejemplo). */
class NotFoundException(message: String) : Exception(message)
