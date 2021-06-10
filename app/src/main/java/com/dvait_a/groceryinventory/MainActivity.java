package com.dvait_a.groceryinventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private int tab;

    public FirebaseFirestore firebaseFirestore;
    public ListView listView;
    private ArrayList<item> groceryList, eatablesList, medicsList, cleaningList, otherList;
    private inventoryListAdapter adapter1, adapter2, adapter3, adapter4, adapter5;

    ImageView groceryImg, cleaningImg, eatableImg, medicsImg, otherImg;

    Button requirement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);

        groceryImg = findViewById(R.id.groceryImg);
        cleaningImg = findViewById(R.id.cleaningImg);
        eatableImg = findViewById(R.id.eatableImg);
        medicsImg = findViewById(R.id.medicsImg);
        otherImg = findViewById(R.id.otherImg);

        Button groceryTab = findViewById(R.id.groceryTab);
        Button cleaningTab = findViewById(R.id.cleaningTab);
        Button eatableTab = findViewById(R.id.eatableTab);
        Button medicsTab = findViewById(R.id.medicsTab);
        
        Button otherTab = findViewById(R.id.otherTab);

        Button addItem = findViewById(R.id.addItem);

        requirement=findViewById(R.id.add_grocery);

        groceryList = new ArrayList<>();
        cleaningList = new ArrayList<>();
        medicsList = new ArrayList<>();
        otherList = new ArrayList<>();
        eatablesList = new ArrayList<>();


        setLists();

        groceryTab.setOnClickListener(v -> {
            tab = 1;
            listView.setAdapter(adapter1);
            groceryImg.setBackgroundColor(Color.GRAY);
            cleaningImg.setBackgroundColor(Color.TRANSPARENT);
            eatableImg.setBackgroundColor(Color.TRANSPARENT);
            medicsImg.setBackgroundColor(Color.TRANSPARENT);
            otherImg.setBackgroundColor(Color.TRANSPARENT);
        });
        cleaningTab.setOnClickListener(v -> {
            tab = 2;
            listView.setAdapter(adapter2);
            cleaningImg.setBackgroundColor(Color.GRAY);
            groceryImg.setBackgroundColor(Color.TRANSPARENT);
            eatableImg.setBackgroundColor(Color.TRANSPARENT);
            medicsImg.setBackgroundColor(Color.TRANSPARENT);
            otherImg.setBackgroundColor(Color.TRANSPARENT);
        });
        eatableTab.setOnClickListener(v -> {
            tab = 3;
            listView.setAdapter(adapter3);
            eatableImg.setBackgroundColor(Color.GRAY);
            cleaningImg.setBackgroundColor(Color.TRANSPARENT);
            groceryImg.setBackgroundColor(Color.TRANSPARENT);
            medicsImg.setBackgroundColor(Color.TRANSPARENT);
            otherImg.setBackgroundColor(Color.TRANSPARENT);
        });
        medicsTab.setOnClickListener(v -> {
            tab = 4;
            listView.setAdapter(adapter4);
            medicsImg.setBackgroundColor(Color.GRAY);
            cleaningImg.setBackgroundColor(Color.TRANSPARENT);
            eatableImg.setBackgroundColor(Color.TRANSPARENT);
            groceryImg.setBackgroundColor(Color.TRANSPARENT);
            otherImg.setBackgroundColor(Color.TRANSPARENT);
        });
        otherTab.setOnClickListener(v -> {
            tab = 5;
            listView.setAdapter(adapter5);
            otherImg.setBackgroundColor(Color.GRAY);
            cleaningImg.setBackgroundColor(Color.TRANSPARENT);
            eatableImg.setBackgroundColor(Color.TRANSPARENT);
            medicsImg.setBackgroundColor(Color.TRANSPARENT);
            groceryImg.setBackgroundColor(Color.TRANSPARENT);
        });

        requirement.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,RequirementActivity.class);
            startActivity(intent);
        });


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecord(tab);
            }
        });
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editRecord(tab, position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteRecord(tab,position);
                return false;
            }
        });
    }

    public void addRecord(int tab) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.inventory_list_add_record, viewGroup, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        final EditText itemLabel, itemCount;
        Button submitRecord;

        itemLabel = dialogView.findViewById(R.id.itemName);
        itemCount = dialogView.findViewById(R.id.itemCount);

        submitRecord = dialogView.findViewById(R.id.submitRecord);

        CollectionReference collectionReference = null;
        switch (tab) {
            case 1:
                collectionReference = firebaseFirestore.collection("Grocery");
                break;
            case 2:
                collectionReference = firebaseFirestore.collection("Cleaning");
                break;
            case 3:
                collectionReference = firebaseFirestore.collection("Eatables");
                break;
            case 4:
                collectionReference = firebaseFirestore.collection("Medics");
                break;
            case 5:
                collectionReference = firebaseFirestore.collection("Others");
                break;
        }

        CollectionReference finalCollectionReference = collectionReference;
        submitRecord.setOnClickListener(v -> {
            item items = new item(itemLabel.getText().toString(), Integer.parseInt(itemCount.getText().toString()));
            finalCollectionReference.add(items).addOnSuccessListener(documentReference -> Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show());
            alertDialog.dismiss();

        });
        setLists();

    }

    void deleteRecord(int tab,int pos){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.delete_entry_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Button cancel,remove;

        remove=dialogView.findViewById(R.id.continue1);
        cancel=dialogView.findViewById(R.id.deleteRecord);


        remove.setOnClickListener(v -> {
            CollectionReference collectionReference;
            item items;
            switch (tab){
                case 1:collectionReference=firebaseFirestore.collection("Grocery");
                    items=groceryList.get(pos);
                    break;
                case 2:collectionReference=firebaseFirestore.collection("Cleaning");
                    items=cleaningList.get(pos);
                    break;
                case 3:collectionReference=firebaseFirestore.collection("Eatables");
                    items=eatablesList.get(pos);
                    break;
                case 4:collectionReference=firebaseFirestore.collection("Medics");
                    items=medicsList.get(pos);
                    break;
                case 5:collectionReference=firebaseFirestore.collection("Others");
                    items=otherList.get(pos);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + tab);
            }

            Query query=collectionReference.whereEqualTo("itemName",items.itemName);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot snapshot:task.getResult()){
                        snapshot.getReference().delete();
                        alertDialog.dismiss();
                        setLists();
                    }
                }
            });
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }


    void editRecord(int tab,int pos){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.inventory_list_add_record, viewGroup, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        final EditText itemLabel,itemCount;
        Button submitRecord;

        itemLabel=dialogView.findViewById(R.id.itemName);
        itemCount=dialogView.findViewById(R.id.itemCount);

        submitRecord=dialogView.findViewById(R.id.submitRecord);

        CollectionReference collectionReference;
        item items;
        switch (tab){
            case 1:collectionReference=firebaseFirestore.collection("Grocery");
            items=groceryList.get(pos);
            break;
            case 2:collectionReference=firebaseFirestore.collection("Cleaning");
            items=cleaningList.get(pos);
            break;
            case 3:collectionReference=firebaseFirestore.collection("Eatables");
            items=eatablesList.get(pos);
            break;
            case 4:collectionReference=firebaseFirestore.collection("Medics");
            items=medicsList.get(pos);
            break;
            case 5:collectionReference=firebaseFirestore.collection("Others");
            items=otherList.get(pos);
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + tab);
        }

        itemLabel.setText(items.getItemName());
        itemCount.setText(String.valueOf(items.getItemCount()));

        CollectionReference finalCollectionReference = collectionReference;
        submitRecord.setOnClickListener(v -> {
            item items1 =new item(itemLabel.getText().toString(),Double.parseDouble(itemCount.getText().toString()));
            finalCollectionReference.whereEqualTo("itemName",itemLabel.getText().toString())
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot:task.getResult()){
                                String id=documentSnapshot.getId();
                                items1.itemCount=Double.parseDouble(itemCount.getText().toString());
                                finalCollectionReference.document(id).set(items1);
                                alertDialog.dismiss();
                            }
                        }
                    });
        });
        setLists();

    }


        void setView(String collection,int tab){
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(collection).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(queryDocumentSnapshots.isEmpty()){
                Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }else{
                for(DocumentSnapshot documentSnapshot :queryDocumentSnapshots){

                    String name=documentSnapshot.getString("itemName");
                    double count=Double.parseDouble(String.valueOf(documentSnapshot.get("itemCount")));

                    item items1=new item(name,count);

                    switch (tab){
                        case 1:groceryList.add(items1);
                        break;
                        case 2:cleaningList.add(items1);
                            break;
                        case 3:eatablesList.add(items1);
                            break;
                        case 4:medicsList.add(items1);
                            break;
                        case 5:otherList.add(items1);
                            break;
                    }
                }
            }
        }).addOnFailureListener(e -> {

        });
    }

    void setLists(){
        setView("Grocery",1);
        groceryList.clear();
        adapter1=new inventoryListAdapter(MainActivity.this,R.layout.inventory_list_adapter_view,groceryList);
        setView("Cleaning",2);
        cleaningList.clear();
        adapter2=new inventoryListAdapter(MainActivity.this,R.layout.inventory_list_adapter_view,cleaningList);
        setView("Eatables",3);
        eatablesList.clear();
        adapter3=new inventoryListAdapter(MainActivity.this,R.layout.inventory_list_adapter_view,eatablesList);
        setView("Medics",4);
        medicsList.clear();
        adapter4=new inventoryListAdapter(MainActivity.this,R.layout.inventory_list_adapter_view,medicsList);
        setView("Others",5);
        otherList.clear();
        adapter5=new inventoryListAdapter(MainActivity.this,R.layout.inventory_list_adapter_view,otherList);

        listView.setAdapter(adapter1);
        tab=1;
        groceryImg.setBackgroundColor(Color.GRAY);
        cleaningImg.setBackgroundColor(Color.TRANSPARENT);
        eatableImg.setBackgroundColor(Color.TRANSPARENT);
        medicsImg.setBackgroundColor(Color.TRANSPARENT);
        otherImg.setBackgroundColor(Color.TRANSPARENT);
    }

}
class inventoryListAdapter extends ArrayAdapter<item> {

    Context context;
    List<item> passwordRecordList;

    public inventoryListAdapter(Context context, int resource, List<item> passwordRecordList) {
        super(context, resource, passwordRecordList);
        this.context=context;
        this.passwordRecordList=passwordRecordList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view=inflater.inflate(R.layout.inventory_list_adapter_view,null);

        TextView passwordLabel,passwordValue;

        passwordLabel=view.findViewById(R.id.itemNameText);
        passwordValue=view.findViewById(R.id.itemCountText);

        item record=passwordRecordList.get(position);

        passwordLabel.setText(record.getItemName());
        passwordValue.setText(String.valueOf(record.getItemCount()));

        return view;
    }
}
