package com.example.tamarind;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class bottom_sheet_more extends BottomSheetDialogFragment {
    private BottomSheetListener bottomSheetListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_more, container, false);

        TextView suggestions = v.findViewById(R.id.suggestions);

        suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onButtonClick("Suggestions");
                dismiss();
            }
        });
        return v;
    }

    public interface BottomSheetListener {
        void onButtonClick(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            bottomSheetListener = (BottomSheetListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " implement bottomSheet Listener");
        }

    }
}
