package com.example.scannerbluetooh;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView textoExibirNome = findViewById(R.id.texto_exibir_nome);
        TextView textoExibirMac = findViewById(R.id.texto_exibir_mac);
        Button botaoRetornar = findViewById(R.id.botao_retornar);

        String nomeRecebido = getIntent().getStringExtra("CHAVE_NOME_DISPOSITIVO");
        String macRecebido = getIntent().getStringExtra("CHAVE_ENDERECO_MAC");

        String textoNomeFinal = "Nome: " + (nomeRecebido != null ? nomeRecebido : "Indisponível");
        String textoMacFinal = "MAC: " + (macRecebido != null ? macRecebido : "Indisponível");

        textoExibirNome.setText(textoNomeFinal);
        textoExibirMac.setText(textoMacFinal);

        botaoRetornar.setOnClickListener(visao -> finish());
    }
}