package com.exapmle.firebase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.exapmle.firebase.R;
import com.exapmle.firebase.adapters.TicketAdapter;
import com.exapmle.firebase.firebase.AuthRepository;
import com.exapmle.firebase.firebase.DataCallback;
import com.exapmle.firebase.firebase.TicketRepository;
import com.exapmle.firebase.models.Ticket;
import com.exapmle.firebase.utils.IntentKeys;
import com.exapmle.firebase.utils.UiUtils;

import java.util.List;

public class MyTicketsActivity extends AppCompatActivity implements TicketAdapter.OnTicketClickListener {
    private final TicketRepository ticketRepository = new TicketRepository();
    private final AuthRepository authRepository = new AuthRepository();
    private TicketAdapter ticketAdapter;
    private View loadingOverlay;
    private View layoutEmpty;
    private android.widget.TextView txtStateMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadingOverlay = findViewById(R.id.loadingOverlay);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        txtStateMessage = findViewById(R.id.txtStateMessage);

        androidx.recyclerview.widget.RecyclerView recyclerTickets = findViewById(R.id.recyclerTickets);
        recyclerTickets.setLayoutManager(new LinearLayoutManager(this));
        ticketAdapter = new TicketAdapter(this);
        recyclerTickets.setAdapter(ticketAdapter);

        loadTickets();
    }

    private void loadTickets() {
        if (authRepository.getCurrentFirebaseUser() == null) {
            finish();
            return;
        }
        setLoading(true);
        ticketRepository.getTicketsForUser(authRepository.getCurrentFirebaseUser().getUid(), new DataCallback<>() {
            @Override
            public void onSuccess(List<Ticket> data) {
                setLoading(false);
                ticketAdapter.submitList(data);
                txtStateMessage.setText(data.isEmpty() ? getString(R.string.empty_tickets_message) : getString(R.string.toolbar_tickets_subtitle));
                layoutEmpty.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                txtStateMessage.setText(getString(R.string.error_load_tickets));
                layoutEmpty.setVisibility(View.VISIBLE);
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void setLoading(boolean loading) {
        loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTicketClick(Ticket ticket) {
        Intent intent = new Intent(this, TicketDetailActivity.class);
        intent.putExtra(IntentKeys.EXTRA_TICKET_ID, ticket.getTicketId());
        startActivity(intent);
    }
}
