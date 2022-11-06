package com.example.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter(private var stores: MutableList<StoreEntity>, private var listener: OnClickListener) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>(){

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
         val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        //siempre retornar el viewHolder Personalizado de la classe inner o interna
      //retorna la vista que inflamos
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores.get(position)
        //ahora trabajar son ese objeto
        with(holder){
            //solo trabajamos con una propiedad del obj
            setListner(store)
            binding.tvName.text = store.name

            binding.cbFavorite.isChecked = store.isFavorite

            //agregar la imagen al imagephoto
            Glide.with(mContext)
                .load(store.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgPhoto)

            store.photoUrl = binding.imgPhoto.toString()


        }

    }

    //para agregar la store
    override fun getItemCount(): Int = stores.size

    fun add(storeEntity: StoreEntity) {
        //si no existe la store creada
        if (!stores.contains(storeEntity)){
            //agrega la store al arreglo general del adapter
            stores.add(storeEntity)
            //refresque los cambios y en donde
            notifyItemInserted(stores.size-1)
        }

    }

    fun setStores(stores: MutableList<StoreEntity>) {
        this.stores = stores
        notifyDataSetChanged()
    }

    //para actualizar el adaptador en general y se guarde el corazoncito
    fun update(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if (index!= -1){
            stores.set(index,storeEntity)
            //para que solo actualize el cuadrito que haya sido afectado
            notifyItemChanged(index)
        }

    }

    fun delete(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if (index!= -1){
            stores.removeAt(index)
            //para que solo actualize el cuadrito que haya sido afectado
            notifyItemRemoved(index)
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val binding = ItemStoreBinding.bind(view)

        fun setListner(storeEntity: StoreEntity){
            //with o con siginifica que al principio tod empieza con binding.root
            with(binding.root){
                setOnClickListener(){ listener.onClick(storeEntity.id)}
                setOnLongClickListener {
                    listener.onDeleteStore(storeEntity)
                    true
                }
            }

            binding.cbFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }

        }
    }
}