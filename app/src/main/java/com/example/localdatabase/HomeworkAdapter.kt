package com.example.localdatabase

class HoneworkAdapter (private val onitemClickCallback: OnItemClickCallback):

    RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder>() {
    var listHomework = ArrayList<Homework>()

    set(listHomework) {

        if (listHomework.size > 0) {
            this.listHomework.clear()
        }
        this.listHomework.addAll(listHomework)

        }

    interface OnItemClickCallback {

        fun onItemClicked(selectedHomework: Homework?, position: Int?)
    }
    fun addItem(homework: Homework){
        this.listHomework.add(homework)
        notifyItemInserted(this.listHomework.size - 1)
    }
    fun updateItem(position: Int, homework: Homework) {

        this.listHomework [position] = honework

        notifyItemChanged(position, homework)

    }

    fun removeIten(position: Int) {

        this.listHomework.removeAt(position)

        notifyItenRemoved (position)

        notifyItemRangeChanged(position, this.ListHomework.size)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_homework, parent, attachToRoot = false)
        return HomeworkViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeworkViewHolder, position: Int) {
        holder.bind(listHomework[position])
    }

    override fun getItemCount(): Int = this.listHomework.size

    inner class HomeworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemHomeworkBinding.bind(itemView)

        fun bind(homework: Homework) {
            binding.tvItemTitle.text = homework.title
            binding.tvItemDate.text = homework.date
            binding.tvItemDescription.text = homework.description
            binding.cvItemHomework.setOnClickListener {
                onItemClickCallback.onItemClicked(homework, adapterPosition)
            }
        }
    }

}

