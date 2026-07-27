# 67 PAY S.A. — Sistema Bancário Digital

Sistema bancário digital com API REST em Java/Spring Boot e interface web em React/TypeScript. O projeto possui experiências específicas para cliente, gerente e administrador.

## Tecnologias

### Backend

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- H2 Database
- Maven Wrapper
- Springdoc OpenAPI

### Frontend

- React
- TypeScript
- Vite
- Tailwind CSS
- Axios
- React Router DOM

## Requisitos

- Java 21 ou superior
- Node.js 20 ou superior
- IntelliJ IDEA ou Eclipse (opcional, para executar a API por uma IDE)

> Não é necessário instalar o Maven manualmente. O projeto já possui o Maven Wrapper (`mvnw` e `mvnw.cmd`), que baixa a versão necessária automaticamente.

## Instalação dos requisitos pelo terminal

Após instalar, feche e abra o terminal novamente. Para conferir as versões, execute:

```bash
java -version
node --version
npm --version
```

### Windows

Abra o PowerShell como administrador e execute:

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
winget install OpenJS.NodeJS.LTS
```

Caso o comando `winget` não esteja disponível, instale o Java 21 e o Node.js LTS pelos sites oficiais e reinicie o terminal.

### macOS

Com o Homebrew instalado, execute:

```bash
brew install openjdk@21
brew install node
```

Se o Java não for reconhecido após a instalação, siga a mensagem exibida pelo Homebrew para adicionar o JDK ao sistema.

### Linux Ubuntu/Debian

Execute:

```bash
sudo apt update
sudo apt install openjdk-21-jdk
sudo apt install nodejs npm
```

Se a versão do Node.js fornecida pela sua distribuição for antiga, instale a versão LTS atual pelo site oficial do Node.js.

## Como executar

Você precisa iniciar a API e o frontend separadamente.

### Passo 1 — Abrir o projeto

Abra a pasta do projeto no IntelliJ IDEA ou no seu editor de código.

### Passo 2 — Iniciar a API pelo IntelliJ IDEA

1. Abra o arquivo:

   ```text
   src/main/java/br/com/ufca/sixsevenpayapi/SixSevenPayApiApplication.java
   ```

2. Clique no ícone verde de execução ao lado da classe `SixSevenPayApiApplication`.
3. Escolha **Run 'SixSevenPayApiApplication'**.
4. Aguarde a mensagem indicando que a aplicação iniciou na porta `8080`.

A API estará disponível em:

```text
http://localhost:8080
```

### Alternativa: iniciar a API pelo Eclipse

1. Abra o Eclipse.
2. Selecione **File > Import > Maven > Existing Maven Projects**.
3. Em **Root Directory**, selecione a pasta principal do projeto.
4. Selecione o projeto encontrado e clique em **Finish**.
5. No explorador de projetos, abra:

   ```text
   src/main/java/br/com/ufca/sixsevenpayapi/SixSevenPayApiApplication.java
   ```

6. Clique com o botão direito no arquivo.
7. Selecione **Run As > Java Application**.
8. Escolha `SixSevenPayApiApplication`, caso o Eclipse solicite.

Quando a inicialização terminar, a API estará em `http://localhost:8080`.

### Alternativa: iniciar a API pelo terminal

Se não estiver usando o IntelliJ, abra um terminal na pasta principal do projeto e execute:

```bash
./mvnw spring-boot:run
```

No Windows, use:

```powershell
.\mvnw.cmd spring-boot:run
```

### Passo 3 — Iniciar o frontend

Abra um segundo terminal na pasta principal do projeto. Execute os comandos abaixo, um por vez:

```bash
cd frontend
```

Na primeira vez que executar o projeto, instale as dependências:

```bash
npm install
```

Depois, inicie o frontend:

```bash
npm run dev
```

O terminal mostrará um endereço semelhante a:

```text
http://localhost:5173
```

Abra esse endereço no navegador para utilizar o sistema.

## Banco de dados

O sistema usa o banco H2. Os dados ficam persistidos localmente na pasta:

```text
database/paydb.mv.db
```

## Perfis do sistema

### Cliente

- Cadastro e login.
- Consulta de conta corrente e conta poupança.
- Depósito, saque e transferências por número da conta.
- Transferência entre contas próprias.
- Extrato de transações.
- Solicitação de conta poupança.
- Solicitação de crédito e pagamento de fatura.
- Visualização de cartão, faturas e compras.
- Compra no crédito com número do cartão, CVV e senha de transação.
- Atualização de perfil e senha.
- Solicitação de encerramento de conta sem informar ID manualmente.

### Gerente

- Consulta de solicitações pendentes.
- Aprovação ou rejeição de solicitações usando credenciais de gerente.
- Consulta de clientes e respectivas contas.
- Bloqueio e ativação de contas.
- Consulta de transações gerais com titular da conta.
- Consulta de contas negativadas.
- Atualização dos próprios dados.

### Administrador

- Métricas globais do banco.
- Cadastro de gerentes.
- Edição de nome, e-mail e telefone de gerentes.
- Desativação de gerentes.
- Listas separadas de gerentes ativos e desativados.
- Reativação automática de gerente desativado quando o mesmo CPF é cadastrado novamente.
- Atualização da taxa de rendimento das contas poupança.

## API — principais grupos de rotas

| Grupo | Base | Descrição |
| --- | --- | --- |
| Autenticação | `/api/auth` | Login, cadastro, senha e encerramento. |
| Clientes | `/api/customers` | Perfil e contas do cliente. |
| Contas | `/api/accounts` | Saldo, depósitos, saques, transferências e extrato. |
| Cartões | `/api/credit-cards` | Compras, faturas, pagamentos e cartão do cliente. |
| Solicitações | `/api/requests` | Poupança, crédito, aprovação e rejeição. |
| Gerência | `/api/managers` | Relatórios, contas e solicitações pendentes. |
| Administração | `/api/admin` | Métricas, gerentes e taxa de juros. |

Documentação OpenAPI, quando a API estiver em execução:

```text
http://localhost:8080/swagger-ui/index.html
```
