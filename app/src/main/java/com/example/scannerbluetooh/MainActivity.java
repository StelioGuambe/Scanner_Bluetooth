package com.example.scannerbluetooh;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int CODIGO_PEDIDO_PERMISSAO = 1001;

    private BluetoothAdapter adaptadorBluetooth;
    private final ArrayList<String> listaParaExibir = new ArrayList<>();
    private final ArrayList<BluetoothDevice> listaObjetosDispositivos = new ArrayList<>();
    private ArrayAdapter<String> adaptadorDaLista;

    private EditText campoFiltroTexto;
    private Button botaoIniciarBusca;
    private ProgressBar barraProgressoBusca;
    private ListView listaDispositivosEncontrados;

    private boolean novosDispositivosEncontrados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View vistaPrincipal = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(vistaPrincipal, (visao, recortes) -> {
            Insets barrasDoSistema = recortes.getInsets(WindowInsetsCompat.Type.systemBars());
            visao.setPadding(barrasDoSistema.left, barrasDoSistema.top, barrasDoSistema.right, barrasDoSistema.bottom);
            return recortes;
        });

        // configuracao da Toolbar no topo
        MaterialToolbar barraTopApp = findViewById(R.id.barra_top_app);
        setSupportActionBar(barraTopApp);

        // usar o TextView centralizado do layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        campoFiltroTexto = findViewById(R.id.campo_filtro_texto);
        botaoIniciarBusca = findViewById(R.id.botao_iniciar_busca);
        barraProgressoBusca = findViewById(R.id.barra_progresso_busca);
        listaDispositivosEncontrados = findViewById(R.id.lista_dispositivos_encontrados);

        adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();

        adaptadorDaLista = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaParaExibir) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View visao = super.getView(position, convertView, parent);
                TextView textoItem = visao.findViewById(android.R.id.text1);
                textoItem.setTextColor(Color.WHITE);
                return visao;
            }
        };
        listaDispositivosEncontrados.setAdapter(adaptadorDaLista);

        solicitarPermissoesAcesso();
        carregarDispositivosPareados();

        botaoIniciarBusca.setOnClickListener(visao -> realizarPesquisaBluetooth());

        campoFiltroTexto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequencia, int inicio, int contagem, int depois) {}

            @Override
            public void onTextChanged(CharSequence sequencia, int inicio, int antes, int contagem) {
                adaptadorDaLista.getFilter().filter(sequencia);
            }

            @Override
            public void afterTextChanged(Editable editavel) {}
        });

        listaDispositivosEncontrados.setOnItemClickListener((pai, visao, posicao, id) -> {
            if (posicao < listaObjetosDispositivos.size()) {
                BluetoothDevice dispositivoSelecionado = listaObjetosDispositivos.get(posicao);
                Intent intencaoMudarTela = new Intent(MainActivity.this, DetailActivity.class);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    String nomeDispositivo = dispositivoSelecionado.getName() != null ? dispositivoSelecionado.getName() : "Sem Nome";
                    String enderecoMac = dispositivoSelecionado.getAddress();

                    intencaoMudarTela.putExtra("CHAVE_NOME_DISPOSITIVO", nomeDispositivo);
                    intencaoMudarTela.putExtra("CHAVE_ENDERECO_MAC", enderecoMac);
                    startActivity(intencaoMudarTela);
                }
            }
        });

        IntentFilter filtroBusca = new IntentFilter();
        filtroBusca.addAction(BluetoothDevice.ACTION_FOUND);
        filtroBusca.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filtroBusca.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receptorTransmissaoBluetooth, filtroBusca);
    }

    private void carregarDispositivosPareados() {
        if (adaptadorBluetooth != null && adaptadorBluetooth.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                Set<BluetoothDevice> pareados = adaptadorBluetooth.getBondedDevices();
                if (pareados != null) {
                    for (BluetoothDevice dispositivo : pareados) {
                        String nome = dispositivo.getName() != null ? dispositivo.getName() : "Sem Nome";
                        String mac = dispositivo.getAddress();
                        String itemFormatado = nome + " (Pareado)\nMAC: " + mac;

                        if (!listaParaExibir.contains(itemFormatado)) {
                            listaParaExibir.add(itemFormatado);
                            listaObjetosDispositivos.add(dispositivo);
                        }
                    }
                    adaptadorDaLista.notifyDataSetChanged();
                }
            }
        }
    }

    private void realizarPesquisaBluetooth() {
        if (adaptadorBluetooth == null) {
            Toast.makeText(this, "Bluetooth não é suportado neste dispositivo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!adaptadorBluetooth.isEnabled()) {
            Toast.makeText(this, getString(R.string.mensagem_ativar_bluetooth), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!verificarPermissoesConcedidas()) {
            solicitarPermissoesAcesso();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (adaptadorBluetooth.isDiscovering()) {
                adaptadorBluetooth.cancelDiscovery();
            }

            novosDispositivosEncontrados = false;

            boolean iniciou = adaptadorBluetooth.startDiscovery();
            if (!iniciou) {
                Toast.makeText(this, "Erro ao iniciar a pesquisa de Bluetooth.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final BroadcastReceiver receptorTransmissaoBluetooth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contexto, Intent intencao) {
            String acaoRecebida = intencao.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(acaoRecebida)) {
                barraProgressoBusca.setVisibility(View.VISIBLE);
                botaoIniciarBusca.setEnabled(false);
                Toast.makeText(contexto, getString(R.string.mensagem_pesquisando), Toast.LENGTH_SHORT).show();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(acaoRecebida)) {
                barraProgressoBusca.setVisibility(View.GONE);
                botaoIniciarBusca.setEnabled(true);

                if (!novosDispositivosEncontrados) {
                    Toast.makeText(contexto, "Nenhum novo dispositivo foi encontrado.", Toast.LENGTH_SHORT).show();
                }

            } else if (BluetoothDevice.ACTION_FOUND.equals(acaoRecebida)) {
                BluetoothDevice dispositivoEncontrado = intencao.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (dispositivoEncontrado != null) {
                    if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        String nome = dispositivoEncontrado.getName();

                        if (nome != null && !nome.trim().isEmpty()) {
                            String mac = dispositivoEncontrado.getAddress();
                            String itemFormatado = nome + "\nMAC: " + mac;

                            if (!listaParaExibir.contains(itemFormatado)) {
                                listaParaExibir.add(itemFormatado);
                                listaObjetosDispositivos.add(dispositivoEncontrado);
                                adaptadorDaLista.notifyDataSetChanged();
                                novosDispositivosEncontrados = true;
                            }
                        }
                    }
                }
            }
        }
    };

    private boolean verificarPermissoesConcedidas() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void solicitarPermissoesAcesso() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            }, CODIGO_PEDIDO_PERMISSAO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PEDIDO_PERMISSAO) {
            carregarDispositivosPareados();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receptorTransmissaoBluetooth);
        } catch (IllegalArgumentException excecao) {
            excecao.printStackTrace();
        }
    }
}