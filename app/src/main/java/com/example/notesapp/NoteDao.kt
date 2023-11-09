package com.example.notesapp

import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth

class NoteDao {
    private val db = FirebaseFirestore.getInstance()
    val noteCollection = db.collection("Notes")

    private val auth = Firebase.auth

    fun addNote(note:String){
        val currentUserId = auth.currentUser!!.uid
        val note = Note(text, currentUserId)
        noteCollection.document().set(note)
    }
}