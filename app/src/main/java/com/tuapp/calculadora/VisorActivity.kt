// En el adaptador del ViewPager2 usamos PhotoView
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val fotoFile = listaFotos[position]
    holder.photoView.setImageURI(Uri.fromFile(fotoFile))
    
    /
}
