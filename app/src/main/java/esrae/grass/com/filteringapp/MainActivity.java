package esrae.grass.com.filteringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import Adapter.CategoryAdapter;
import Adapter.FilterCollectionAdapter;
import Model.Category;
import Model.FilterModel;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnTextClickListener {

    ImageView originalImage, cancelImage, downloadImage;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    RecyclerView filterCollectionRecyle,categoryCollectionRecyle;
    Bitmap originalBitmap;
    ProgressBar progressBar;
    public String categoriesPassData;
    int save_pos;

    public static int change_pos;
    public ArrayList<Category> categoryArrayList=new ArrayList<>();
    public FilterCollectionAdapter filterAdapter;
    ArrayList<FilterModel> colorfilter =new ArrayList<>();
    ArrayList<FilterModel> random_filterList=new ArrayList<>();
    ArrayList<FilterModel> type_filterList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildXML();

        originalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                       requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_CODE);
                    }
                    else
                    {
                        pickImageFromGalery();
                    }
                }
                else
                {
                    pickImageFromGalery();
                    //apk  versiyonu düşük ise ;
                }    }
        });


        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Image Cancellation");
                builder.setIcon(R.drawable.alert_icon);
                builder.setMessage("The selected image will be canceled and a new image will be selected. ");
                builder.setNegativeButton("NOT CONFIRM ", null);
                builder.setPositiveButton("CONFIRM ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        originalImage.setImageResource(R.drawable.no_image_pic);
                        originalImage.setClickable(true);
                        if(CategoryAdapter.click==true)
                        {
                            colorfilter.clear();
                            random_filterList.clear();
                            type_filterList.clear();
                            filterAdapter.notifyDataSetChanged();
                            return;
                        }

                    }
                });

                builder.show();

            }
        });
        setCategoryRecyleOperation();

        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (categoriesPassData)
                {
                    case "Color" :
                        saveFilterImageGalery(save_pos);
                        MediaStore.Images.Media.insertImage(getContentResolver(), colorfilter.get(save_pos).getBitmap(),"filteredImage", "changefiltered");
                        Toast.makeText(MainActivity.this, "Save your galery", Toast.LENGTH_SHORT).show();
                        break;
                    case "Random" :
                        saveFilterImageGalery(save_pos);
                        MediaStore.Images.Media.insertImage(getContentResolver(),random_filterList.get(save_pos).getBitmap(),"filteredImage", "changefiltered");
                        Toast.makeText(MainActivity.this, "Save your galery", Toast.LENGTH_SHORT).show();
                        break;
                    case "Type" :
                        saveFilterImageGalery(save_pos);
                        MediaStore.Images.Media.insertImage(getContentResolver(),type_filterList.get(save_pos).getBitmap(),"filteredImage", "changefiltered");
                        Toast.makeText(MainActivity.this, "Save your galery", Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Closing Notification");
        builder.setIcon(R.drawable.alert_icon);
        builder.setMessage("Do you want to close the application? ");
        builder.setNegativeButton("NO ", null);
        builder.setPositiveButton("YES ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }

    private void buildXML() {
        originalImage = findViewById(R.id.originalImage);
        cancelImage = findViewById(R.id.cancelIcon);
        downloadImage = findViewById(R.id.downloadIcon);
        filterCollectionRecyle=findViewById(R.id.filterCollectionRecyle);
        categoryCollectionRecyle=findViewById(R.id.categoryCollectionRecyle);
        progressBar=findViewById(R.id.progresBar);
    }

    private void pickImageFromGalery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, IMAGE_PICK_CODE);
    }

    private void setCategoryRecyleOperation() {
        final CategoryAdapter categoryAdapter;
        categoryArrayList.add(new Category("Color"));
        categoryArrayList.add(new Category("Random"));
        categoryArrayList.add(new Category("Type"));


        categoryCollectionRecyle.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryCollectionRecyle.setLayoutManager(layoutManager);
        categoryAdapter=new CategoryAdapter(categoryArrayList,this);
        categoryCollectionRecyle.setAdapter(categoryAdapter);

    }

    public void getAsyncTaskResult(ArrayList<FilterModel>[] filter)
    {
        colorfilter=filter[0];
        random_filterList=filter[1];
        type_filterList=filter[2];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode)
       {
           case PERMISSION_CODE :
               if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
               {
                   pickImageFromGalery();
               }
               else
               {
                   Toast.makeText(this, "Permission Denied..!", Toast.LENGTH_SHORT).show();
               }
               break;
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE) {
            Uri uri = data.getData();
            originalImage.setImageURI(uri);
            originalImage.setClickable(false);
            try {
                originalBitmap= MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                LUTApplyTask lutApplyTask=new LUTApplyTask(this);

                lutApplyTask.execute(originalBitmap);
                Toast.makeText(getApplicationContext(),"Click on any of the filter options above.",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        }



    private  static class LUTApplyTask extends AsyncTask<Bitmap, Void, ArrayList<FilterModel>[]>
    {
        private WeakReference<MainActivity> activityWeakReference;
        Bitmap originalBitmap;
        ArrayList<FilterModel> colorfilter=new ArrayList<>();
        ArrayList<FilterModel> random_filterList=new ArrayList<>();
        ArrayList<FilterModel> type_filterList=new ArrayList<>();


        public LUTApplyTask(MainActivity mainActivity) {
           activityWeakReference=new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity mainActivity=activityWeakReference.get();
            if(mainActivity==null || mainActivity.isFinishing())
            {
                return;
            }
            mainActivity.originalImage.setVisibility(View.INVISIBLE);
            mainActivity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<FilterModel>[] doInBackground(Bitmap... bitmaps) {
            originalBitmap=bitmaps[0];
            ArrayList <FilterModel> [] modal= new ArrayList[3];
            fillList();
            modal[0]=colorfilter;
            modal[1]=random_filterList;
            modal[2]=type_filterList;

            return modal;
        }

        @Override
        protected void onPostExecute(ArrayList<FilterModel>[] bitmaps) {
            super.onPostExecute(bitmaps);
            MainActivity mainActivity=activityWeakReference.get();
            if(mainActivity==null || mainActivity.isFinishing())
            {
                return;
            }

            mainActivity.progressBar.setVisibility(View.INVISIBLE);
            mainActivity.originalImage.setVisibility(View.VISIBLE);
            mainActivity.getAsyncTaskResult(bitmaps);

        }
        private void fillList() {
            MainActivity mainActivity=activityWeakReference.get();
            if(mainActivity==null || mainActivity.isFinishing())
            {
                return;
            }
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.mono_1)),
                    "Mono_1"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.mono_2)),
                    "Mono_2"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.mono_3)),
                    "Mono_3"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.orange_bl_2)),
                    "OB_2"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.orange_bl_3)),
                    "OB_3"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.orange_neob2)),
                    "OBN_2"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.orange_bl_3)),
                    "OBN_3"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.mono_gr_1)),
                    "MGreen_1"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.mono_gr_2)),
                    "MGreen_2"));
            colorfilter.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.mono_gr_3)),
                    "MGreen_3"));

            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_1)),
                    "Filmic_1"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_2)),
                    "Filmic_2"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_3)),
                    "Filmic_3"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_4)),
                    "Filmic_4"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_5)),
                    "Filmic_5"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_6)),
                    "Filmic_6"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_7)),
                    "Filmic_7"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_8)),
                    "Filmic_8"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_9)),
                    "Filmic_9"));
            random_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.filmic_10)),
                    "Filmic_10"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.simple_2)),
                    "Simple_2"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.simple_3)),
                    "Simple_3"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.spectrum_1)),
                    "Spectrum_1"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.spectrum_2)),
                    "Spectrum_2"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.spectrum_3)),
                    "Spectrum_3"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.virtual_01)),
                    "Virtual_1"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.virtual_02)),
                    "Virtual_2"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.virtual_03)),
                    "Virtual_3"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.web_01)),
                    "Web_1"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.web_02)),
                    "Web_2"));
            type_filterList.add(new
                    FilterModel(applUT(originalBitmap,BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.web_03)),
                    "Web_3"));

        }
        public  Bitmap applUT(Bitmap originalBitmap, Bitmap lutBitmap) {
            int lutWidth = lutBitmap.getWidth();
            int lutColors[] = new int[lutWidth * lutBitmap.getHeight()];
            lutBitmap.getPixels(lutColors, 0, lutWidth, 0, 0, lutWidth, lutBitmap.getHeight());

            int mWidth = originalBitmap.getWidth();
            int mHeight = originalBitmap.getHeight();
            int[] pix = new int[mWidth * mHeight];
            originalBitmap.getPixels(pix, 0, mWidth, 0, 0, mWidth, mHeight);

            int R, G, B;
            for (int y = 0; y < mHeight; y++)
                for (int x = 0; x < mWidth; x++) {
                    int index = y * mWidth + x;
                    int r = ((pix[index] >> 16) & 0xff) / 4;
                    int g = ((pix[index] >> 8) & 0xff) / 4;
                    int b = (pix[index] & 0xff) / 4;

                    int lutIndex = getLutIndex(lutWidth, r, g, b);

                    R = ((lutColors[lutIndex] >> 16) & 0xff);
                    G = ((lutColors[lutIndex] >> 8) & 0xff);
                    B = ((lutColors[lutIndex]) & 0xff);
                    pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
                }
            Bitmap filteredBitmap = Bitmap.createBitmap(mWidth, mHeight, originalBitmap.getConfig());
            filteredBitmap.setPixels(pix, 0, mWidth, 0, 0, mWidth, mHeight);
            return filteredBitmap;
        }

        private int getLutIndex(int lutWidth, int redDepth, int greenDepth, int blueDepth) {
            int lutX = (blueDepth % 8) * 64 + redDepth;
            int lutY = (blueDepth / 8) * 64 + greenDepth;
            return lutY * lutWidth + lutX;
        }

    }

    @Override
    public void onTextClick(String data) {
        Log.e("Datam",data);
        categoriesPassData=data;
        Log.e("New Data",categoriesPassData);
        if(originalBitmap==null)
        {
            Toast.makeText(this, "Please select picture !! ", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (categoriesPassData)
        {
            case "Color" :
                filterCollectionRecyle.setHasFixedSize(true);
                filterCollectionRecyle.setItemViewCacheSize (15);
                filterCollectionRecyle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                filterAdapter=new FilterCollectionAdapter(colorfilter);
                filterAdapter.setHasStableIds(true);
                filterCollectionRecyle.setAdapter(filterAdapter);
                filterAdapter.setOnItemClickListener(new FilterCollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        saveFilterImageGalery(position);
                        change_pos=position;
                        filterAdapter.notifyDataSetChanged();
                        originalImage.setImageBitmap(colorfilter.get(position).getBitmap());

                    }
                });
                break;
            case "Random" :
                filterCollectionRecyle.setHasFixedSize(true);
                filterCollectionRecyle.setItemViewCacheSize (15);
                filterCollectionRecyle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                filterAdapter=new FilterCollectionAdapter(random_filterList);
                filterAdapter.setHasStableIds(true);
                filterCollectionRecyle.setAdapter(filterAdapter);
                filterAdapter.setOnItemClickListener(new FilterCollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        saveFilterImageGalery(position);
                        change_pos=position;
                        filterAdapter.notifyDataSetChanged();
                        originalImage.setImageBitmap(random_filterList.get(position).getBitmap());

                    }
                });

                break;
            case "Type" :
                filterCollectionRecyle.setHasFixedSize(true);
                filterCollectionRecyle.setItemViewCacheSize (15);
                filterCollectionRecyle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                filterAdapter=new FilterCollectionAdapter(type_filterList);
                filterAdapter.setHasStableIds(true);
                filterCollectionRecyle.setAdapter(filterAdapter);
                filterAdapter.setOnItemClickListener(new FilterCollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        saveFilterImageGalery(position);
                        change_pos=position;
                        filterAdapter.notifyDataSetChanged();
                        originalImage.setImageBitmap(type_filterList.get(position).getBitmap());
                    }
                });

                break;
                default:
                    break;
        }
    }

    private void saveFilterImageGalery(int position) {
            save_pos=position;
    }

}
