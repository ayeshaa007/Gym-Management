package com.ayesha.gym_management.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.ayesha.gym_management.R;
import com.ayesha.gym_management.adapters.MemberAdapter;
import com.gymmanagement.app.database.DatabaseHelper;
import com.gymmanagement.app.models.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberListActivity extends AppCompatActivity implements MemberAdapter.OnMemberClickListener {

    private RecyclerView recyclerView;
    private MemberAdapter memberAdapter;
    private DatabaseHelper databaseHelper;
    private EditText etSearch;
    private ChipGroup chipGroupFilter;
    private Chip chipAll, chipActive, chipExpired;
    private TextView tvMemberCount;

    private List<Member> allMembers;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        databaseHelper = new DatabaseHelper(this);

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        setupSearch();
        setupFilter();
        loadMembers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMembers();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewMembers);
        etSearch = findViewById(R.id.etSearch);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        chipAll = findViewById(R.id.chipAll);
        chipActive = findViewById(R.id.chipActive);
        chipExpired = findViewById(R.id.chipExpired);
        tvMemberCount = findViewById(R.id.tvMemberCount);

        chipAll.setChecked(true);
    }

    private void setupRecyclerView() {
        memberAdapter = new MemberAdapter(this, new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(memberAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMembers();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilter() {
        chipGroupFilter.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.chipAll) {
                    currentFilter = "All";
                } else if (checkedId == R.id.chipActive) {
                    currentFilter = "Active";
                } else if (checkedId == R.id.chipExpired) {
                    currentFilter = "Expired";
                }
                filterMembers();
            }
        });
    }

    private void loadMembers() {
        allMembers = databaseHelper.getAllMembers();
        filterMembers();
    }

    private void filterMembers() {
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();
        List<Member> filteredList = new ArrayList<>();

        for (Member member : allMembers) {
            boolean matchesSearch = member.getFullName().toLowerCase().contains(searchQuery) ||
                    member.getPhone().contains(searchQuery);

            if (!matchesSearch) continue;

            String status = member.getMembershipStatus();
            boolean matchesFilter = false;

            if (currentFilter.equals("All")) {
                matchesFilter = true;
            } else if (currentFilter.equals("Active")) {
                matchesFilter = status.equals("Active") || status.equals("Expiring Soon");
            } else if (currentFilter.equals("Expired")) {
                matchesFilter = status.equals("Expired");
            }

            if (matchesFilter) {
                filteredList.add(member);
            }
        }

        memberAdapter.updateList(filteredList);
        tvMemberCount.setText(filteredList.size() + " " + getString(R.string.members_found));
    }

    @Override
    public void onEditClick(Member member) {
        Intent intent = new Intent(this, EditMemberActivity.class);
        intent.putExtra("member_id", member.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(final Member member) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_member_title)
                .setMessage(R.string.delete_member_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteMember(member.getId());
                        Toast.makeText(MemberListActivity.this, R.string.member_deleted_success, Toast.LENGTH_SHORT).show();
                        loadMembers();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}