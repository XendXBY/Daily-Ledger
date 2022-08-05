package com.xendxby.dailyledger;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xendxby.dailyledger.database.LedgerDBHelper;
import com.xendxby.dailyledger.entity.LedgerItemInfo;
import com.xendxby.dailyledger.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class LedgerDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private LedgerDBHelper ledgerDBHelper;
    private BigDecimal totalIncome;
    private BigDecimal totalOutcome;
    private LinearLayout ll_ledger_items;
    private Button btn_return;

    private Map<Integer, View> idToViewMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_detail);

        ledgerDBHelper = LedgerDBHelper.getInstance(this);
        ll_ledger_items = findViewById(R.id.ll_ledger_items);
        btn_return = findViewById(R.id.btn_return);

        btn_return.setOnClickListener(this);

        ledgerDBHelper.openReadLink();
        showLedgerItems();
        ledgerDBHelper.closeLink();
        updatePriceState();

    }

    @SuppressLint("SetTextI18n")
    private void showLedgerItems() {
        List<LedgerItemInfo> infos = ledgerDBHelper.getAllLedgerItems();
        totalIncome = new BigDecimal("0");
        totalOutcome = new BigDecimal("0");

        for (int i = infos.size() - 1; i >= 0; i--) {
            LedgerItemInfo info = infos.get(i);

            View view = LayoutInflater.from(this).inflate(R.layout.ledger_item, null);

            TextView tv_item_time = view.findViewById(R.id.tv_item_time);
            TextView tv_item_category = view.findViewById(R.id.tv_item_category);
            TextView tv_item_remark = view.findViewById(R.id.tv_item_remark);
            TextView tv_item_price = view.findViewById(R.id.tv_item_price);
            TextView tv_item_delete = view.findViewById(R.id.tv_item_delete);

            String category;
            String remark = info.remark == null || info.remark.length() == 0 ? "无" : info.remark;

            tv_item_time.setText(info.time);
            tv_item_remark.setText(remark);
            tv_item_price.setText(BigDecimalUtil.bigDecimalToString(info.price));

            if (info.type == LedgerItemInfo.RecordTypeEnum.OUTCOME) {
                category = "支出>" + MainActivity.categoryOutcomeNames[info.category];
                tv_item_category.setText(category);
                tv_item_price.setTextColor(getResources().getColor(R.color.red_outcome));
                totalOutcome = totalOutcome.add(info.price);
            } else {
                category = "收入>" + MainActivity.categoryIncomeNames[info.category];
                tv_item_category.setText(category);
                tv_item_price.setTextColor(getResources().getColor(R.color.green_income));
                totalIncome = totalIncome.add(info.price);
            }

            tv_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LedgerDetailActivity.this);

                    builder.setTitle("确定要删除该条账目吗？");

                    builder.setMessage(
                            "类型：" + category + "\n" +
                                    "金额：" + BigDecimalUtil.bigDecimalToString(info.price) + "\n" +
                                    "备注：" + remark
                    );

                    builder.setPositiveButton("删除", (dialogInterface, i1) -> {
                        ll_ledger_items.removeView(view);

                        if (info.type == LedgerItemInfo.RecordTypeEnum.OUTCOME) {
                            totalOutcome = totalOutcome.subtract(info.price);
                        } else {
                            totalIncome = totalIncome.subtract(info.price);
                        }

                        updatePriceState();

                        ledgerDBHelper.openWriteLink();
                        ledgerDBHelper.deleteLedgerItem(info.id);
                        ledgerDBHelper.closeLink();
                    });

                    builder.setNegativeButton("取消", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            ll_ledger_items.addView(view);
        }
    }

    private void updatePriceState() {
        TextView tv_total_income = findViewById(R.id.tv_total_income);
        TextView tv_total_outcome = findViewById(R.id.tv_total_outcome);
        TextView tv_current_balance = findViewById(R.id.tv_current_balance);

        BigDecimal currentBalance = new BigDecimal(totalIncome.toString());
        currentBalance = currentBalance.subtract(totalOutcome);

        tv_total_income.setText(BigDecimalUtil.bigDecimalToString(totalIncome));
        tv_total_outcome.setText(BigDecimalUtil.bigDecimalToString(totalOutcome));
        tv_current_balance.setText(BigDecimalUtil.bigDecimalToString(currentBalance));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        finish();
    }
}