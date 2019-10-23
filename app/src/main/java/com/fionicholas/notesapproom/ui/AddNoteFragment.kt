package com.fionicholas.notesapproom.ui


import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.Navigation

import com.fionicholas.notesapproom.R
import com.fionicholas.notesapproom.db.Note
import com.fionicholas.notesapproom.db.NoteDatabase
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class AddNoteFragment : BaseFragment() {

    private var note: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            note = AddNoteFragmentArgs.fromBundle(it).note
            edit_title.setText(note?.title)
            edit_note.setText(note?.note)
        }

        btn_save.setOnClickListener {   view ->
            val noteTitle = edit_title.text.toString().trim()
            val noteBody = edit_note.text.toString().trim()

            if (noteTitle.isEmpty()) {
                edit_title.error = "title required"
                edit_title.requestFocus()
                return@setOnClickListener
            }
            if (noteBody.isEmpty()) {
                edit_note.error = "note required"
                edit_note.requestFocus()
                return@setOnClickListener
            }

            launch {

                context?.let {
                    val mNote = Note(noteTitle, noteBody)

                    if(note == null) {
                        NoteDatabase(it).getNoteDao().addNote(mNote)
                        it.toast("Note Saved")
                    }else{
                        mNote.id = note!!.id
                        NoteDatabase(it).getNoteDao().updateNote(mNote)
                        it.toast("Note Updated")
                    }
                    val action = AddNoteFragmentDirections.actionSaveNote()
                    Navigation.findNavController(view).navigate(action)
                }
            }


        }
    }

    private fun deleteNote(){
        AlertDialog.Builder(context).apply {
            setTitle("Are You Sure?")
            setMessage("You Can't Undo this operation")
            setPositiveButton("Yes"){_ ,_ ->
                launch {
                    NoteDatabase(context).getNoteDao().deleteNote(note!!)
                    val action = AddNoteFragmentDirections.actionSaveNote()
                    Navigation.findNavController(view!!).navigate(action)
                }
            }
            setNegativeButton("No"){_ ,_ ->

            }
        }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete -> if(note != null) deleteNote() else context?.toast("Can't Delete")
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

}
