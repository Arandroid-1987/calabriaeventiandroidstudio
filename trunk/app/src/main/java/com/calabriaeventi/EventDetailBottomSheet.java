package com.calabriaeventi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.calabriaeventi.model.Evento;
import com.calabriaeventi.utils.ActionCommon;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     EventDetailBottomSheet.newInstance(evento).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class EventDetailBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String EVENTO_EXTRA = "EVENTO_EXTRA";
    private Evento evento;

    public static EventDetailBottomSheet newInstance(Evento evento) {
        final EventDetailBottomSheet fragment = new EventDetailBottomSheet();
        final Bundle args = new Bundle();
        args.putSerializable(EVENTO_EXTRA, evento);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail_bottom_sheet, container, false);

        Bundle args = getArguments();
        if (args != null) {
            evento = (Evento) args.getSerializable(EVENTO_EXTRA);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.action_add_calendar).setOnClickListener(this);
        view.findViewById(R.id.action_share).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.action_share:
                ActionCommon.share(evento, getActivity());
                break;
            case R.id.action_add_calendar:
                ActionCommon.addToCalendar(evento,  getActivity());
                break;
        }
    }
}
