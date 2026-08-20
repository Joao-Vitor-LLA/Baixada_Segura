# Baixada Segura

Aplicativo Android para monitoramento colaborativo de áreas alagadas em tempo real.

O projeto permite que usuários reportem pontos de alagamento diretamente no mapa, compartilhando as informações instantaneamente com outros usuários através do Firebase Realtime Database.

---

# Funcionalidades

* Visualização de mapa em tempo real usando OSMDroid
* Localização atual do usuário
* Criação de alertas de alagamento
* Compartilhamento realtime entre dispositivos
* Adição manual de coordenadas
* Exibição do endereço da ocorrência
* Sistema de confirmação de alertas
* Interface interativa com marcadores e áreas destacadas

---

# Tecnologias Utilizadas

* Kotlin
* Android Studio
* Firebase Realtime Database
* OSMDroid
* Geocoder Android API

---

# Estrutura do Projeto

```text
app/
 ├── java/com/example/baixadasegura/
 │    ├── MainActivity.kt
 │    ├── CoordenadaActivity.kt
 │    ├── AlagamentoInfoWindow.kt
 |    ├── LoginActivity.kt
 |    ├── CadastroActivity.kt
 │
 ├── res/layout/
 │    ├── activity_main.xml
 │    ├── activity_coordenada.xml
 │    ├── info_alagamento.xml
 │    ├── info_login.xml
 │    ├── info_cadastro.xml
 │
 ├── AndroidManifest.xml
```

---

# Configuração do Firebase

## 1. Criar projeto no Firebase

Acesse:

https://console.firebase.google.com/

Crie um novo projeto.

---

## 2. Adicionar aplicativo Android

Package name utilizado:

```text
com.example.baixadasegura
```

---

## 3. Baixar google-services.json

Após criar o app:

* Baixe o arquivo `google-services.json`
* Coloque em:

```text
app/google-services.json
```

---

## 4. Ativar Realtime Database

No Firebase:

```text
Build → Realtime Database
```

Crie o banco em modo teste.

---

# Dependências

No arquivo `build.gradle.kts (Module: app)`:

```kotlin
dependencies {

    implementation("org.osmdroid:osmdroid-android:6.1.16")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    implementation("com.google.firebase:firebase-database")

    implementation("com.google.firebase:firebase-auth-ktx")
}
```

---

# Permissões Necessárias

No `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

---

# Como Executar

## 1. Clonar repositório

```bash
git clone https://github.com/Joao-Vitor-LLA/Baixada_Segura.git
```

---

## 2. Abrir no Android Studio

```text
File → Open
```

Selecione a pasta do projeto.

---

## 3. Sincronizar Gradle

Espere o Android Studio baixar as dependências.

---

## 4. Rodar o aplicativo

Conecte um celular Android ou use um emulador.

Clique em:

```text
Run ▶
```

---

# Como Usar

## Adicionar alerta pela localização atual

Clique no botão:

```text
📍
```

---

## Adicionar alerta manualmente

Clique no botão:

```text
🗺️
```

Digite latitude e longitude.

---

## Adicionar alerta pressionando o mapa

Ative:

```text
📌
```

Depois pressione o mapa por alguns segundos.

---

# Sistema Realtime

Os alertas são sincronizados automaticamente utilizando:

```kotlin
addValueEventListener()
```

Qualquer alteração no Firebase é atualizada em todos os dispositivos conectados.

---

# Melhorias Futuras

* Upload de fotos
* Sistema de votação persistente
* Rotas alternativas
* Notificações push
* Machine Learning para detecção automática
* Integração com APIs meteorológicas

---

# Desenvolvedores

Arthur Almeida Lima arthurlima@unisantos.br

Daniel Santiago Purificação danielpurificacao@unisantos.br

João Pedro Lira de Carvalho jcarvalho@unisantos.br

João Vitor Ludovino Leite Alves j.alves@unisantos.br

Nicolas Caldeira dos Santos nicolassantos@unisantos.br

Nicolas Jimenes Haase nicolashaase@unisantos.br

