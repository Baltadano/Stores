package com.example.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class EditStoreFragment : Fragment() {

        private lateinit var mBinding: FragmentEditStoreBinding
        private var mActivity: MainActivity? = null

        private var mIsEditMode: Boolean = false
        private var mStoreEntity: StoreEntity? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
         ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id),0)
        if(id != null && id !=0L){
        mIsEditMode = true

        getStore(id)
        //Toast.makeText(activity,"$id",Toast.LENGTH_SHORT).show()
        }else{
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "")
        }


        //por que MainActivity hereda de appcompat cosas que utilizaremos
        mActivity = activity as? MainActivity
        //agregar una flecha de retroseso

        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)


        //decirle que tenga acceso al menu
        setHasOptionsMenu(true)
        mBinding.etPhotoUrl.addTextChangedListener {
            //this es el contexto del fragmento
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }

    }

    private fun getStore(id: Long) {
        doAsync {
            mStoreEntity = StoreAplication.dataBase.storeDao().getStoreById(id)
            uiThread {
                // !! significa que no es null
                if(mStoreEntity != null)setUiStore(mStoreEntity!!)

            }
        }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
            with(mBinding){
                etName.text = storeEntity.name.editable()
                etPhone.text = storeEntity.phone.editable()
                etWebsite.text = storeEntity.website.editable()
                etPhotoUrl.text = storeEntity.photoUrl.editable()

                Glide.with(requireActivity())//deberia ir activity!!
                    .load(storeEntity.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(imgPhoto)
            }
    }

    // metodo que sirve para en update edite los datos del objeto y no solo asigne a los campos
    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //un recurso de tipo menu y un menu
        inflater.inflate(R.menu.menu_save,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            //que pasara al dar la flecha hacia atras
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            //que pasara cuando le demos en el boton check(menu)
            R.id.action_save -> {

                if(mStoreEntity != null){

                    /*val store = StoreEntity(name = mBinding.etName.text.toString().trim(),
                        phone = mBinding.etPhone.text.toString().trim(),
                        website = mBinding.etWebsite.text.toString().trim(),
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    )*/
                    with(mStoreEntity!!){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()

                    }
                    doAsync {
                        if(mIsEditMode) StoreAplication.dataBase.storeDao().updateStore(mStoreEntity!!)
                        else mStoreEntity!!.id = StoreAplication.dataBase.storeDao().addStore(mStoreEntity!!)
                        uiThread {
                            hideKeyboard()
                            if(mIsEditMode){
                                mActivity?.updateStore(mStoreEntity!!)
                                Snackbar.make(mBinding.root, R.string.edit_store_message_update_succes, Snackbar.LENGTH_SHORT).show()
                            }else {
                                //del activiti main ejecuta el metodo nuevo pero desde el fragment para que lo haga con el btn guardar
                                mActivity?.addStore(mStoreEntity!!)


                                //almacene los datos  de la tienda, por lo tanto un mensaje
                                /*Snackbar.make(mBinding.root,R.string.edit_store_message_save_succes,Snackbar.LENGTH_SHORT).show()
                                */
                                Toast.makeText(mActivity,
                                    R.string.edit_store_message_save_succes,
                                    Toast.LENGTH_SHORT).show()

                                //al guardar retorn a la vista anterior o presionas back en esta accion tambien
                                mActivity?.onBackPressed()
                            }
                        }
                    }

                }

                true
            }else -> super.onOptionsItemSelected(item)
        }
        //return super.onOptionsItemSelected(item)
    }

    //donde se devincula nuestra vista osea cuando nos salimos del fragment
    //oculta el teclado
    private fun hideKeyboard(){
        val imn = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // nos adseguramos que no sea null con requireview
        imn.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {


        Log.i("Actividad", "Teclado se cierra")
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }
    override fun onDestroy() {

            //funcion de regresar con el menu desviculada
            mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            //nombre de la barra superior cambiada a la principal
            mActivity?.supportActionBar?.title = getString(R.string.app_name)
            mActivity?.hideFav(true)

        //menu desviculado
        setHasOptionsMenu(false)

        //Para acceder a un elemento del activity.main.xml desde un fregment es con una interface auxiliar


        super.onDestroy()
    }

}