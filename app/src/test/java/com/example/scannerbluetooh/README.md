# Scanner Bluetooth

Turma: 3L6LASIR2T  
Tema:SCANNER BLUETOOTH


## Integrantes do Grupo
* Alcides Alexandre Fondo
* Carolina Hamile Lourino Chamane
* Lercio Domingos Chambal
* Stelio Faustino Guambe

## Descrição da Aplicação
O **Scanner Bluetooth** é uma aplicação Android desenvolvida em Java para varredura, deteção e filtragem em tempo real de dispositivos Bluetooth próximos. A aplicação permite listar dispositivos já pareados e novos dispositivos descobertos no ambiente, filtrá-los dinamicamente por nome ou endereço MAC e visualizar os detalhes técnicos do dispositivo selecionado numa segunda ecrã.

##  Funcionalidades Implementadas
* **Listagem Automática de Pareados:** Exibe os dispositivos Bluetooth previamente pareados ao iniciar o app.
* **Busca Dinâmica de Novos Dispositivos:** Varredura em tempo real via `BroadcastReceiver` (`ACTION_FOUND`).
* **Filtro Dinâmico:** Pesquisa rápida por nome ou endereço MAC via `TextWatcher`.
* **Notificação de Estado:** Notificação por `Toast` e barra de progresso (`ProgressBar`) indicando início, fim e quando nenhum novo dispositivo é encontrado.
* **Detalhes do Dispositivo:** Navegação para uma segunda ecrã (`DetailActivity`) com as informações detalhadas do dispositivo selecionado.
* **Gestão Dinâmica de Permissões:** Solicitação de permissões em tempo de execução para Android 12 ou superior (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`).

## Tecnologias Utilizadas
* **Linguagem:** Java 17+
* **Plataforma:** Android SDK (Suporte a Android 12+ / API 31+)
* **Ambiente de Desenvolvimento:** Android Studio
* **Interface:** XML Layouts, Material Design (`MaterialToolbar`), `ListView`, `EditText`, `ProgressBar`
* **Arquitetura & Componentes:** `AppCompatActivity`, `BroadcastReceiver`, `Intent`, `ArrayAdapter`

## Permissões Utilizadas
As seguintes permissões foram declaradas no `AndroidManifest.xml` e geridas em runtime:
* `android.permission.BLUETOOTH`
* `android.permission.BLUETOOTH_ADMIN`
* `android.permission.BLUETOOTH_SCAN`
* `android.permission.BLUETOOTH_CONNECT`
* `android.permission.ACCESS_FINE_LOCATION` (para compatibilidade com versões do Android inferiores a 12)

## Instruções para Executar o Projeto

### Pré-requisitos
1. **Android Studio** (versão Iguana, Jellyfish ou superior instalada).
2. Dispositivo Android físico com Bluetooth ativado (recomendado) ou Emulador Android configurado.

### Passos de Execução
1. Clone este repositório para o seu computador:
   ```bash
   git clone [https://github.com/StelioGuambe/Scanner_Bluetooth.git](https://github.com/StelioGuambe/Scanner_Bluetooth.git)