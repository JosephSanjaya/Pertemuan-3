package com.ukm.firebaseintegration

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ukm.firebaseintegration.databinding.FragmentSecondBinding
import com.ukm.firebaseintegration.models.BulbStatus
import com.ukm.firebaseintegration.models.UsersData

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var isLightBulbOn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        firestore = Firebase.firestore
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBulb()
        binding.buttonLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        binding.buttonVerify.setOnClickListener {
            doVerify()
        }
        binding.buttonAddUser.setOnClickListener {
            addUserToFirebase()
        }
        binding.buttonLoad.setOnClickListener {
            loadDataUser("KsA4mgIH3BnrWSJFtdBK")
        }
        binding.buttonBulb.setOnClickListener {
            toggleBulbStatus()
        }
    }

    private fun toggleBulbStatus() {
        firestore.collection("iot_param")
            .document("bulb_status")
            .update("isOn", !isLightBulbOn)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    isLightBulbOn = !isLightBulbOn
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT)
                        .show()
                }
                else Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun observeBulb() {
        Firebase.firestore.collection("iot_param")
            .document("bulb_status")
            .addSnapshotListener { value, error ->
                if (error == null) {
                    val data = value?.toObject(BulbStatus::class.java)
                    isLightBulbOn = data?.isOn == true
                    val tint = ContextCompat.getColor(
                        requireContext(),
                        if (isLightBulbOn) R.color.teal_200 else R.color.black
                    )
                    binding.imageBulb.imageTintList = ColorStateList.valueOf(tint)
                }
            }
    }

    private fun addUserToFirebase() {
        firestore.collection("users")
            .add(
                UsersData(
                    "Joseph",
                    "Teacher",
                    "https://upload.wikimedia.org/wikipedia/commons/9/9a/Gull_portrait_ca_usa.jpg"
                )
            ).addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) Toast.makeText(context, "Success", Toast.LENGTH_SHORT)
                    .show()
                else Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun loadDataUser(userId: String) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {
                    val data = it.result.toObject(UsersData::class.java)
                    binding.textviewName.text = data?.name
                    binding.textviewRole.text = data?.role
                    binding.imageProfile.load(data?.profilePicture)
                } else Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun doVerify() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) Toast.makeText(context, "Email Send", Toast.LENGTH_SHORT)
                    .show()
                else Toast.makeText(context, "Email Failed to Send", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}