package com.example.goodsapp.data

import com.example.goodsapp.R

/**
 * Provides the list of dummy contacts.
 * This sample implements this as constants, but real-life apps should use a database and such.
 */
data class Contact(val id: Int, val name: String) {

    val icon = R.mipmap.logo_avatar

    companion object {
        /**
         * Representative invalid contact ID.
         */
        val invalidId = -1

        /**
         * The contact ID.
         */
        val id = "contact_id"

        /**
         * The list of dummy contacts.
         */
        val contacts = arrayOf(
            Contact(1,"Tereasa"),
            Contact(2,"Chang"),
            Contact(3,"Kory"),
            Contact(4,"Clare"),
            Contact(5,"Landon"),
            Contact(6,"Kyle"),
            Contact(7,"Deana"),
            Contact(8,"Daria"),
            Contact(9,"Melisa"),
            Contact(10,"Sammie")
        )

        /**
         * Finds a [Contact] specified by a contact ID.
         *
         * @param id The contact ID. This needs to be a valid ID.
         * @return A [Contact]
         */
        fun byId(id: Int) = contacts[id]
    }
}
