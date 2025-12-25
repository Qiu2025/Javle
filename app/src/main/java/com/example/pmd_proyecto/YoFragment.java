package com.example.pmd_proyecto;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;

import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YoFragment extends Fragment {

    private ActivityResultLauncher<String> seleccionarGaleria;
    private ActivityResultLauncher<Uri> tomarFoto;
    private Uri uriFotoCamara;
    private ImageView avatarView;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public YoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoFragment newInstance(String param1, String param2) {
        YoFragment fragment = new YoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        seleccionarGaleria = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        guardarAvatar(uri);
                    }
                }
        );

        tomarFoto = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && uriFotoCamara != null) {
                        guardarAvatar(uriFotoCamara);
                    }
                }
        );



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences prefs =
                requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);

        boolean logged = prefs.getBoolean("logged", false);

        if (logged) {
            return cargarVistaPerfil(inflater, container, prefs);
        } else {
            return cargarVistaInvitado(inflater, container);
        }
    }

    private View cargarVistaInvitado(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragmento_yo_guest, container, false);

        view.findViewById(R.id.btnLogin).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), LoginActivity.class))
        );

        view.findViewById(R.id.btnRegister).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RegisterActivity.class))
        );

        return view;
    }

    private View cargarVistaPerfil(LayoutInflater inflater,
                                   ViewGroup container,
                                   SharedPreferences prefs) {

        View view = inflater.inflate(R.layout.fragment_yo, container, false);

        avatarView = view.findViewById(R.id.imageView);
        TextView tvEmail = view.findViewById(R.id.tvEmailPerfil);
        TextView tvNombre = view.findViewById(R.id.tvNombrePerfil);

        avatarView.setOnClickListener(v -> mostrarOpcionesAvatar());

        String email = prefs.getString("email", null);

        if (email != null) {
            tvEmail.setText(email);

            String nombre = email.contains("@")
                    ? email.substring(0, email.indexOf("@"))
                    : email;
            tvNombre.setText(nombre);

            DBHelper dbHelper = new DBHelper(requireContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor c = db.rawQuery(
                    "SELECT avatar FROM usuarios WHERE email = ?",
                    new String[]{email}
            );

            if (c.moveToFirst()) {
                String avatarUriString = c.getString(0);
                if (avatarUriString != null) {
                    avatarView.setImageURI(Uri.parse(avatarUriString));
                }
            }
            c.close();
        }

        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            prefs.edit().clear().apply();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new YoFragment())
                    .commit();
        });

        return view;
    }

    private void guardarAvatar(Uri uri) {
        if (!isAdded()) return;

        Uri localUri = copiarImagenAInterno(uri);
        if (localUri == null) return;

        SharedPreferences prefs =
                requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);

        String email = prefs.getString("email", null);
        if (email == null) return;

        DBHelper dbHelper = new DBHelper(requireContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("avatar", localUri.toString());

        db.update(
                "usuarios",
                values,
                "email = ?",
                new String[]{email}
        );

        // Recargar perfil
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new YoFragment())
                .commit();
    }



    private Uri crearUriFoto() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "avatar_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        return requireContext()
                .getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    private void mostrarOpcionesAvatar() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cambiar avatar")
                .setItems(new CharSequence[]{"Cámara", "Galería"}, (dialog, which) -> {
                    if (which == 0) { // Cámara
                        uriFotoCamara = crearUriFoto();
                        if (uriFotoCamara != null) {
                            tomarFoto.launch(uriFotoCamara);
                        }
                    } else if (which == 1) { // Galería
                        seleccionarGaleria.launch("image/*");
                    }
                })
                .show();
    }

    private Uri copiarImagenAInterno(Uri sourceUri) {
        try {
            InputStream in = requireContext().getContentResolver().openInputStream(sourceUri);
            File file = new File(requireContext().getFilesDir(),
                    "avatar_" + System.currentTimeMillis() + ".jpg");

            OutputStream out = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            out.close();
            return Uri.fromFile(file);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





}