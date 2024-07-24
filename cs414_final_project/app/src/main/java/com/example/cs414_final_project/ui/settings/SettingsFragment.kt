package com.example.cs414_final_project.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.cs414_final_project.R
import android.content.Context

class SettingsFragment : Fragment() {
    private lateinit var cbDescription: CheckBox
    private lateinit var cbHighlights: CheckBox
    private lateinit var cbAccolades: CheckBox
    private lateinit var btnSave: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        cbDescription = view.findViewById(R.id.cb_description)
        cbHighlights = view.findViewById(R.id.cb_highlights)
        cbAccolades = view.findViewById(R.id.cb_accolades)
        btnSave = view.findViewById(R.id.btn_save)
        loadCheckboxStates()
        btnSave.setOnClickListener {
            saveCheckboxStates()
        }

        return view
    }
    private fun saveCheckboxStates() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("description_checked", cbDescription.isChecked)
        editor.putBoolean("highlights_checked", cbHighlights.isChecked)
        editor.putBoolean("accolades_checked", cbAccolades.isChecked)

        editor.apply()
    }
    private fun loadCheckboxStates() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        cbDescription.isChecked = sharedPreferences.getBoolean("description_checked", true)
        cbHighlights.isChecked = sharedPreferences.getBoolean("highlights_checked", true)
        cbAccolades.isChecked = sharedPreferences.getBoolean("accolades_checked", true)
    }

}