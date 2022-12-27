package com.example.contactsappassignmentrealmdb.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.contactsappassignmentrealmdb.Application;
import com.example.contactsappassignmentrealmdb.R;
import com.example.contactsappassignmentrealmdb.adapters.ContactItemAdapter;
import com.example.contactsappassignmentrealmdb.entity.ModelClass;
import com.example.contactsappassignmentrealmdb.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements ContactItemAdapter.OnItemClickListener {
    private Realm realm;
    private Utils imageUtils;

    private CircleImageView civProfileImg;
    private TextInputEditText etName, etPhoneNumber, etBirthday;
    public static RealmResults<ModelClass> data;
    public static RealmQuery<ModelClass> query;

    private ContactItemAdapter itemAdapter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        realm = Realm.getDefaultInstance();
        Application app = (Application) getApplication();
        //query=realm.where(ModelClass.class);
        query = app.getQuery();

        imageUtils = new Utils();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAddContact = findViewById(R.id.fabAddContact);

        fabAddContact.setOnClickListener(view -> showAddUpdateContactForm(false, null));

        data = query.findAll();

        //declaring adapter
        itemAdapter = new ContactItemAdapter(context, data, this);

        recyclerView.setAdapter(itemAdapter);

        data.addChangeListener(ModelClass -> itemAdapter.notifyDataSetChanged());
    }

    public void showAddUpdateContactForm(Boolean isUpdate, ModelClass modelClass) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_contact_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        etName = dialog.findViewById(R.id.etName);
        etPhoneNumber = dialog.findViewById(R.id.etPhoneNumber);
        etBirthday = dialog.findViewById(R.id.etBirthday);
        civProfileImg = dialog.findViewById(R.id.civProfileImg);
        AppCompatTextView tvUploadPicture = dialog.findViewById(R.id.tvUploadPicture);
        AppCompatImageView ivClose = dialog.findViewById(R.id.ivClose);
        AppCompatButton btnSave = dialog.findViewById(R.id.btnSave);

        dialog.show();

        if (isUpdate) {
            btnSave.setText(getResources().getText(R.string.update));
            if (modelClass != null) {
                etName.setText(modelClass.getName());
                etPhoneNumber.setText(modelClass.getNumber());
                etBirthday.setText(modelClass.getBirthday());
                if (modelClass.getAvtar() != null) {
                    byte[] av = modelClass.getAvtar();
                    if (av.length > 0) {
                        try {
                            Bitmap bm = Utils.byteArray2image(av);
                            civProfileImg.setImageBitmap(bm);
                        } catch (Exception e) {
                            Toast.makeText(context, "error with image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        civProfileImg.setOnClickListener(v -> pickIMage());
        tvUploadPicture.setOnClickListener(v -> pickIMage());
        ivClose.setOnClickListener(view -> {
            refreshFields();
            dialog.dismiss();
        });

        btnSave.setOnClickListener(view -> {
            if (checkValidations()) {
                if (isUpdate && modelClass != null) {
                    updateContact(modelClass);
                } else {
                    insert();
                }

                itemAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

    }

    private boolean checkValidations() {
        String name, phone, birthday;
        name = Objects.requireNonNull(etName.getText()).toString();
        phone = Objects.requireNonNull(etPhoneNumber.getText()).toString();
        birthday = Objects.requireNonNull(etBirthday.getText()).toString();

        if (!name.isEmpty() && !phone.isEmpty() && !birthday.isEmpty()) {
            return true;
        } else if (name.isEmpty()) {
            etName.setError(getResources().getString(R.string.field_must_be_filled));
            return false;
        } else if (phone.isEmpty()) {
            etPhoneNumber.setError(getResources().getString(R.string.field_must_be_filled));
            etName.setError(null);
            return false;
        } else {
            etPhoneNumber.setError(null);
            etBirthday.setError(getResources().getString(R.string.field_must_be_filled));
            return false;
        }
    }

    private void refreshFields() {
        etName.setText("");
        etPhoneNumber.setText("");
        etBirthday.setText("");
    }

    private void insert() {
        final byte[] imgProfile = imageUtils.image2byteArray(civProfileImg);
        realm.executeTransactionAsync(realm -> {
                    ModelClass model = realm.createObject(ModelClass.class);
                    model.setUid(UUID.randomUUID().toString());
                    model.setName(Objects.requireNonNull(etName.getText()).toString());
                    model.setNumber(Objects.requireNonNull(etPhoneNumber.getText()).toString());
                    model.setBirthday(Objects.requireNonNull(etBirthday.getText()).toString());
                    model.setAvtar(imgProfile);
                }, () -> {
                    itemAdapter.notifyDataSetChanged();
                    Toast.makeText(context, "inserted", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(context, "failed: " + error.getMessage(), Toast.LENGTH_SHORT).show());

    }

  /* private List<ModelClass> getData() {
        List<ModelClass> data = new ArrayList<>();
        data.clear();
        data.addAll(realm.where(ModelClass.class).findAll());

        return data;
    }*/

    private void pickIMage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "select image"), 1002);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1002) {
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                civProfileImg.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        data.removeAllChangeListeners();
    }

    @Override
    public void onItemEditClick(View v, int position) {

        Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
        ModelClass model = data.get(position);
        showAddUpdateContactForm(true, model);
    }

    @Override
    public void onItemDeleteClick(View v, int position) {
        Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
        ModelClass model = data.get(position);
        realm.executeTransaction(realm -> {
            RealmResults<ModelClass> result = realm.where(ModelClass.class).equalTo("uid", model.getUid()).findAll();
            result.deleteAllFromRealm();
            itemAdapter.notifyDataSetChanged();
        });
    }

    private void updateContact(ModelClass dataObject) {
        final byte[] imgProfile = imageUtils.image2byteArray(civProfileImg);
        ModelClass modelClass = realm.where(ModelClass.class)
                .equalTo("uid", dataObject.getUid())
                .findFirst();
        realm.beginTransaction();
        modelClass.setName(Objects.requireNonNull(etName.getText()).toString());
        modelClass.setNumber(Objects.requireNonNull(etPhoneNumber.getText()).toString());
        modelClass.setBirthday(Objects.requireNonNull(etBirthday.getText()).toString());
        modelClass.setAvtar(imgProfile);

        realm.commitTransaction();
    }
}