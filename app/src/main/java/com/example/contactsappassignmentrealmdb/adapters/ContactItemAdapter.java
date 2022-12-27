package com.example.contactsappassignmentrealmdb.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactsappassignmentrealmdb.R;
import com.example.contactsappassignmentrealmdb.entity.ModelClass;
import com.example.contactsappassignmentrealmdb.utils.Utils;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmResults;

public class ContactItemAdapter extends RecyclerView.Adapter<ContactItemAdapter.MyViewHolder> {
    private final Context context;
    RealmResults<ModelClass> data;
    private final OnItemClickListener onItemClickListener;

    public ContactItemAdapter(Context ctx, RealmResults<ModelClass> data, OnItemClickListener onItemClickListener) {
        this.context = ctx;
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_contact_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int i) {
        final ModelClass modelClass = data.get(i);
        assert modelClass != null;

        //Note: This type of image loading may can cause ANR while converting a large number of images bytes into bitmaps.
        // We can use background threading to handle this according to our scenario. And when we are working with server we will only save the URl of
        // the image and load image in more efficient way.
        if (modelClass.getAvtar() != null) {
            byte[] av = modelClass.getAvtar();
            if (av.length > 0) {
                try {
                    Bitmap bm = Utils.byteArray2image(av);
                    viewHolder.civProfileImage.setImageBitmap(bm);
                } catch (Exception e) {
                    Toast.makeText(context, "error with image", Toast.LENGTH_SHORT).show();
                }
            }
        }
        viewHolder.tvContactName.setText(modelClass.getName());
        viewHolder.tvContactBirthday.setText(modelClass.getBirthday());
        viewHolder.tvContactPhone.setText(modelClass.getNumber());

        viewHolder.ivEdit.setOnClickListener(view -> onItemClickListener.onItemEditClick(view, i));
        viewHolder.ivDelete.setOnClickListener(view -> onItemClickListener.onItemDeleteClick(view, i));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civProfileImage;
        AppCompatTextView tvContactName, tvContactBirthday, tvContactPhone;
        AppCompatImageView ivEdit, ivDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            civProfileImage = itemView.findViewById(R.id.civProfileImage);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactBirthday = itemView.findViewById(R.id.tvContactBirthday);
            tvContactPhone = itemView.findViewById(R.id.tvContactPhone);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    public interface OnItemClickListener {
        void onItemEditClick(View v, int position);

        void onItemDeleteClick(View v, int position);
    }

}
