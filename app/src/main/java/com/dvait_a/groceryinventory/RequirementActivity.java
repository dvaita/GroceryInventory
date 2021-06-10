package com.dvait_a.groceryinventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequirementActivity extends AppCompatActivity {

    private int tab;

    public ListView listView;
    private ArrayList<item> groceryList, eatablesList, medicsList, cleaningList, otherList;
    private requirementListAdapter adapter1, adapter2, adapter3, adapter4, adapter5;
    private FirebaseDatabase firebaseDatabase;
    ImageView groceryImg, cleaningImg, eatableImg, medicsImg, otherImg;
    DatabaseReference databaseReference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirement);

        firebaseDatabase=FirebaseDatabase.getInstance();

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

        Button addItem=findViewById(R.id.addItem);

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

    void editRecord(int tab,int pos){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(RequirementActivity.this).inflate(R.layout.inventory_list_add_record, viewGroup, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(RequirementActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        final EditText itemLabel,itemCount;
        Button submitRecord;

        itemLabel=dialogView.findViewById(R.id.itemName);
        itemCount=dialogView.findViewById(R.id.itemCount);

        submitRecord=dialogView.findViewById(R.id.submitRecord);

        DatabaseReference databaseReference;
        item items;
        switch (tab){
            case 1:databaseReference=firebaseDatabase.getReference().child("Requirements").child("Grocery");
                items=groceryList.get(pos);
                break;
            case 2:databaseReference=firebaseDatabase.getReference().child("Requirements").child("Cleaning");
                items=cleaningList.get(pos);
                break;
            case 3:databaseReference=firebaseDatabase.getReference().child("Requirements").child("Eatables");
                items=eatablesList.get(pos);
                break;
            case 4:databaseReference=firebaseDatabase.getReference().child("Requirements").child("Medics");
                items=medicsList.get(pos);
                break;
            case 5:databaseReference=firebaseDatabase.getReference().child("Requirements").child("Others");
                items=otherList.get(pos);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tab);
        }

        itemLabel.setText(items.getItemName());
        itemCount.setText(String.valueOf(items.getItemCount()));

        submitRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item items1=new item(itemLabel.getText().toString(),Double.parseDouble(itemCount.getText().toString()));
                databaseReference.orderByChild("itemName").equalTo(items.itemName)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String key=snapshot.getKey();
                                    databaseReference.child(key).setValue(items1);
                                    alertDialog.dismiss();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
      //  setLists();

    }


    public void addRecord(int tab) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(RequirementActivity.this).inflate(R.layout.inventory_list_add_record, viewGroup, false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(RequirementActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        final EditText itemLabel, itemCount;
        Button submitRecord;

        itemLabel = dialogView.findViewById(R.id.itemName);
        itemCount = dialogView.findViewById(R.id.itemCount);

        submitRecord = dialogView.findViewById(R.id.submitRecord);

        DatabaseReference databaseReference=firebaseDatabase.getReference().child("Requirement");
         databaseReference1=firebaseDatabase.getReference();

       submitRecord.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String name=itemLabel.getText().toString();
               if(name.isEmpty()){
                   itemLabel.setError("Empty Field");
               }else{
                   if(itemCount.getText().toString().isEmpty()){
                       itemCount.setError("Empty Count");
                   }else{
                       item item=new item(itemLabel.getText().toString(),Double.parseDouble(itemCount.getText().toString()));
                       switch (tab) {
                           case 1:
                               databaseReference1=databaseReference.child("Grocery");
                               break;
                           case 2:databaseReference1=databaseReference.child("Cleaning");
                               break;
                           case 3:databaseReference1=databaseReference.child("Eatables");
                               break;
                           case 4:databaseReference1=databaseReference.child("Medics");
                               break;
                           case 5:databaseReference1=databaseReference.child("Others");
                               break;
                       }
                       databaseReference1.child(name).setValue(item);
                       alertDialog.dismiss();
                   }
               }
           }
       });


    }
    void deleteRecord(int tab,int pos) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(RequirementActivity.this).inflate(R.layout.delete_requirement_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(RequirementActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Button deleteRecord, continue1;

        continue1 = dialogView.findViewById(R.id.continue1);
        deleteRecord = dialogView.findViewById(R.id.deleteRecord);


        DatabaseReference databaseReference=firebaseDatabase.getReference().child("Requirement");

        deleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (tab) {
                    case 1: deleteDataEntry(databaseReference.child("Grocery"),"Grocery",groceryList.get(pos).itemName);
                        break;
                    case 2:deleteDataEntry(databaseReference.child("Cleaning"),"Cleaning",cleaningList.get(pos).itemName);
                        break;
                    case 3:deleteDataEntry(databaseReference.child("Eatables"),"Eatables",eatablesList.get(pos).itemName);
                        break;
                    case 4:deleteDataEntry(databaseReference.child("Medics"),"Medics",medicsList.get(pos).itemName);
                        break;
                    case 5:deleteDataEntry(databaseReference.child("Others"),"Others",otherList.get(pos).itemName);
                        break;
                }
                alertDialog.dismiss();
                setLists();
            }
        });



        continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (tab) {
                    case 1: deleteDataEntry(databaseReference.child("Grocery"),"Grocery",groceryList.get(pos).itemName);
                    addDataEntry(groceryList.get(pos).itemName,String.valueOf(groceryList.get(pos).itemCount),"Grocery");
                        break;
                    case 2:deleteDataEntry(databaseReference.child("Cleaning"),"Cleaning",cleaningList.get(pos).itemName);
                        addDataEntry(cleaningList.get(pos).itemName,String.valueOf(cleaningList.get(pos).itemCount),"Cleaning");
                        break;
                    case 3:deleteDataEntry(databaseReference.child("Eatables"),"Eatables",eatablesList.get(pos).itemName);
                        addDataEntry(eatablesList.get(pos).itemName,String.valueOf(eatablesList.get(pos).itemCount),"Eatables");
                        break;
                    case 4:deleteDataEntry(databaseReference.child("Medics"),"Medics",medicsList.get(pos).itemName);
                        addDataEntry(medicsList.get(pos).itemName,String.valueOf(medicsList.get(pos).itemCount),"Medics");
                        break;
                    case 5:deleteDataEntry(databaseReference.child("Others"),"Others",otherList.get(pos).itemName);
                        addDataEntry(otherList.get(pos).itemName,String.valueOf(otherList.get(pos).itemCount),"Others");
                        break;
                }

                alertDialog.dismiss();
            }
        });
    }

    void deleteDataEntry(@org.jetbrains.annotations.NotNull DatabaseReference databaseReference, String child, String equalTo){

        Query query=databaseReference.orderByChild("itemName").equalTo(equalTo);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    setLists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void addDataEntry(String name,String count,String collection){
       FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        CollectionReference collectionReference =firebaseFirestore.collection(collection);

        item items = new item(name, Double.parseDouble(count));
        collectionReference.add(items).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(RequirementActivity.this, "Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RequirementActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void setView(String collection,int tab){
       firebaseDatabase=FirebaseDatabase.getInstance();

        Task<DataSnapshot> dataSnapshotTask = firebaseDatabase.getReference().child("Requirement").child(collection).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    item items = snapshot.getValue(item.class);
                    switch (tab) {
                        case 1:
                            assert items != null;
                            groceryList.add(new item(items.itemName, Double.parseDouble(String.valueOf(items.itemCount))));
                            break;
                        case 2:
                            assert items != null;
                            cleaningList.add(new item(items.itemName, Double.parseDouble(String.valueOf(items.itemCount))));
                            break;
                        case 3:
                            assert items != null;
                            eatablesList.add(new item(items.itemName, Double.parseDouble(String.valueOf(items.itemCount))));
                            break;
                        case 4:
                            assert items != null;
                            medicsList.add(new item(items.itemName, Double.parseDouble(String.valueOf(items.itemCount))));
                            break;
                        case 5:
                            assert items != null;
                            otherList.add(new item(items.itemName, Double.parseDouble(String.valueOf(items.itemCount))));
                            break;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RequirementActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setLists(){
        setView("Grocery",1);
        groceryList.clear();
        adapter1=new requirementListAdapter(RequirementActivity.this,R.layout.inventory_list_adapter_view,groceryList);
        setView("Cleaning",2);
        cleaningList.clear();
        adapter2=new requirementListAdapter(RequirementActivity.this,R.layout.inventory_list_adapter_view,cleaningList);
        setView("Eatables",3);
        eatablesList.clear();
        adapter3=new requirementListAdapter(RequirementActivity.this,R.layout.inventory_list_adapter_view,eatablesList);
        setView("Medics",4);
        medicsList.clear();
        adapter4=new requirementListAdapter(RequirementActivity.this,R.layout.inventory_list_adapter_view,medicsList);
        setView("Others",5);
        otherList.clear();
        adapter5=new requirementListAdapter(RequirementActivity.this,R.layout.inventory_list_adapter_view,otherList);

        listView.setAdapter(adapter1);
        tab=1;
        groceryImg.setBackgroundColor(Color.GRAY);
        cleaningImg.setBackgroundColor(Color.TRANSPARENT);
        eatableImg.setBackgroundColor(Color.TRANSPARENT);
        medicsImg.setBackgroundColor(Color.TRANSPARENT);
        otherImg.setBackgroundColor(Color.TRANSPARENT);
    }

}
class requirementListAdapter extends ArrayAdapter<item> {

    Context context;
    List<item> passwordRecordList;


    public requirementListAdapter(Context context, int resource, List<item> passwordRecordList) {
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