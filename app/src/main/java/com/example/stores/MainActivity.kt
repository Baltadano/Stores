package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

//Implementamos la interfaz del adaptador que se llama onClickListener
class MainActivity : AppCompatActivity(), OnClickListener, MainAux{

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        /*mBinding.btnSave.setOnClickListener {
            val storeEntity = StoreEntity( name = mBinding.etName.text.toString().trim())

            Thread{
                StoreAplication.dataBase.storeDao().addStore(storeEntity)
            }.start()
            mAdapter.add(storeEntity)
        }*/
        mBinding.fab.setOnClickListener(){
            launchEditFragment()
        }

        setupRecyclerView()
    }

    private fun launchEditFragment(args: Bundle? = null) {
        //primer paso crear una instancia del frag que queremos lanzar
        val editFragment = EditStoreFragment()

        if (args != null) editFragment.arguments = args


        //manager gestor para controlar los fragmentos
        //transaction decide como es que se va a ejecutar
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        //primero el contenedor, coinatinerMain es el nombre del activiti main. xml
        fragmentTransaction.add(R.id.containerMain, editFragment)
        //tenga la funcionalidad de regresar a la vista anterior
       fragmentTransaction.addToBackStack(null)
        //para que se aplique lo anterior
        fragmentTransaction.commit()

        hideFav()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(),this)
        //Segundo parametro es numero de columnas
        mGridLayout =   GridLayoutManager(this,2)

        getStores()

        mBinding.recyclerView.apply {
            //si el tamanano de la store no cambia optimizamos con
            setHasFixedSize(true)
            //propiedad layoutmanager = a el layout global
            layoutManager = mGridLayout
            //propiedad adapter es igual a la var global
            adapter = mAdapter

        }
    }

    private fun getStores(){
        doAsync {
            val stores = StoreAplication.dataBase.storeDao().getAllStores()
            uiThread {
                //actualizar el adaptador
                mAdapter.setStores(stores)
            }
        }
    }


    override fun onClick(storeId: Long) {
        val args = Bundle()
        //Por que solo pasaremos el ID
        args.putLong(getString(R.string.arg_id), storeId)
        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        //valor de false a true del corazon
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            //actualziamos la tienda
            StoreAplication.dataBase.storeDao().updateStore(storeEntity)
            uiThread {
                //actualizamos el adaptador
                mAdapter.update(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        doAsync {
            StoreAplication.dataBase.storeDao().deleteStore(storeEntity)
            uiThread {
                mAdapter.delete(storeEntity)
            }
        }
    }


    /*
    * MainAuxx
    * */

    override fun hideFav(isVisble: Boolean) {
        if(isVisble) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        //agrega una tienda al adaptador que gestiona el activity principal
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }

}