package com.example.contactsappassignmentrealmdb;

import com.example.contactsappassignmentrealmdb.entity.ModelClass;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;

public class Application extends android.app.Application {

    Realm realm;
    RealmQuery<ModelClass> query;
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration realmConfiguration=new RealmConfiguration.Builder().name("test.db")
                .schemaVersion(1).allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        realm=Realm.getDefaultInstance();
    }

    public RealmQuery<ModelClass> getQuery(){
        return query=realm.where(ModelClass.class);
    }

}
