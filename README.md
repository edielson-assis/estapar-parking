# 🚗 Parking-Garage Simulator API

### Desafio Técnico — Backend Java (Spring Boot)

Uma API REST para gerenciar estacionamentos de garagem simulados. 
O serviço consome dados de um simulador externo, sincroniza vagas/sectors, processa eventos de entrada, estacionamento e saída de veículos, calcula tarifas com base em regras dinâmicas, e permite consultar faturamento por setor.

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3+**
- **Spring Data JPA / Hibernate**
- **Spring Validation**
- **JUnit / Mockito**
- **Lombok**
- **MySQL**
- **Flyway (controle de migrations)**
- **Swagger / OpenAPI 3**
- **Docker / Docker Compose**

---

### 🔎 Visão geral

- Importa automaticamente a estrutura da garagem (setores + vagas) a partir do simulador.
- Recebe eventos do simulador via /webhook: ENTRY, PARKED, EXIT.
- Aplica regras de negócio robustas: vagas únicas, status de vaga, horário de funcionamento por setor, período de carência, preço dinâmico com base na ocupação, etc.
- Armazena histórico de estacionamentos e valores cobrados.
- Permite consultar o faturamento por setor e data.
- Fácil de rodar via Docker.

---

## 🧾 Regras de negócio

- [x] Ao entrar um veículo, marque uma vaga como ocupada
- [x] Ao sair, marque a vaga como disponível e calcule o valor
- [x] Primeiros 30 minutos são grátis
- [x] Após 30 minutos, cobre uma tarifa fixa por hora, inclusive a primeira hora (use `basePrice` da garagem, arredonde para cima)
- [x] Se o estacionamento estiver cheio, não permita novas entradas até liberar uma vaga

### 💰 Regra de preço dinâmico
- [x] Com lotação menor que 25%, desconto de 10% no preço, na hora da entrada
- [x] Com lotação menor até 50%, desconto de 0% no preço, na hora da entrada
- [x] Com lotação menor até 75%, aumentar o preço em 10%, na hora da entrada
- [x] Com lotação menor até 100%, aumentar o preço em 25%, na hora da entrada

### 🚗 Regra de lotação
- [x] Com 100% de lotação, fechar o setor e só permitir mais carros com a saida de um já estacionado



---

## ⚙️ Execução do Projeto

### 🐳 Via Docker Compose


- Docker e Docker Compose instalados no sistema. Você pode baixar o Docker Desktop (que já inclui o Docker Compose) a partir do [site oficial do Docker](https://www.docker.com/).


### Passo 1: Obtenha o arquivo `docker-compose.yml`

Baixe o arquivo `docker-compose.yml` fornecido no repositório. Esse arquivo contém as definições de configuração necessárias para rodar a aplicação e suas dependências, como o banco de dados.

### Passo 2: Execute o Docker Compose

No terminal, navegue até a pasta onde você salvou o `docker-compose.yml` e execute o seguinte comando:

```
docker compose up -d
```

### Passo 3: Verifique os Logs (Opcional)

Para verificar se a aplicação está funcionando corretamente, você pode inspecionar os logs com o comando:

```
docker compose logs -f
```

Esse comando exibirá os logs de todos os containers, permitindo que você veja o status da aplicação e do banco de dados.

### Passo 4: Acesse a Aplicação

Após o Docker Compose iniciar todos os containers, a aplicação estará acessível. Você poderá acessá-la no navegador em:

```
http://localhost:3003/swagger-ui/index.html
```
Isso fará com que a aplicação seja inicializada na porta 3003.

## Parar e Remover os Containers

```
docker compose down
```
Esse comando encerra a execução dos containers e remove os recursos associados, liberando espaço no sistema.

---

## 🧪 Exemplos de uso
### 🔹 Webhook — Receber evento do simulador

```
POST /webhook
Content-Type: application/json

{
  "license_plate": "ZUL0001",
  "event_type": "ENTRY",
  "entry_time": "2025-11-25T12:00:00"
}

```

### 🔹 Consultar faturamento por setor e data

```
POST /revenue
Content-Type: application/json

{
  "date": "2025-01-01",
  "sector": "A"
}
```

### Resposta esperada:

```
{
  "amount": 152.75,
  "currency": "BRL",
  "timestamp": "2025-11-25T23:59:59"
}
```

---

## 👨‍💻 Autor

**Edielson Assis**  
Desenvolvedor Java | Spring Boot  

🔗 [LinkedIn](https://www.linkedin.com/in/edielson-assis)  
💻 [GitHub](https://github.com/edielson-assis)

---

> Projeto desenvolvido com dedicação, seguindo boas práticas e princípios de arquitetura limpa.  
> **“Código limpo é como uma história bem contada — fácil de ler, difícil de esquecer.”**
