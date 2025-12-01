# 🐝 Apicutor - Sistema de Gerenciamento para Apicultura

## Descrição
Apicutor é um sistema completo para gerenciamento de apiários, colmeias e produção apícola, desenvolvido com Spring Boot no backend e Angular no frontend. O sistema oferece uma interface moderna e responsiva com animações suaves e suporte a modo escuro.

## 🚀 Requisitos
- Java 11+
- Node.js 14+
- NPM 6+
- Maven 3.6+

## 📋 Executando o Sistema

### Backend
1. Navegue até a pasta do backend:
```
cd backend
```

2. Execute o projeto Spring Boot:
```
mvn spring-boot:run
```
O backend estará disponível em http://localhost:8080

### Frontend
1. Navegue até a pasta do frontend:
```
cd frontend
```

2. Instale as dependências:
```
npm install
```

3. Execute o projeto Angular:
```
npm start
```
O frontend estará disponível em http://localhost:4200

## ✨ Funcionalidades Principais

### 🏠 Dashboard Moderno
- **Interface Responsiva**: Design adaptável para desktop, tablet e mobile
- **Modo Escuro**: Suporte automático ao modo escuro do sistema
- **Animações Suaves**: Transições e efeitos visuais modernos
- **Cards Animados**: Elementos interativos com hover effects
- **Estatísticas em Tempo Real**: Visualização instantânea de dados

### 🏭 Gerenciamento de Apiários
- Cadastro e edição de apiários
- Visualização com localização
- Controle de colmeias por apiário
- Interface intuitiva com cards modernos

### 🐝 Controle de Colmeias
- Gerenciamento completo de colmeias
- Status dinâmicos (ATIVA, EM_OBSERVACAO, INATIVA, DOENTE, PERDIDA)
- Identificação única e tipos personalizados
- Filtros e busca avançada

### 📊 Dashboard Interativo
- Cards de estatísticas coloridos
- Ações rápidas com botões modernos
- Layout em grid responsivo
- Ícones Font Awesome integrados

### 🔐 Autenticação Segura
- Sistema de login/logout
- JWT token authentication
- Proteção de rotas
- Registro de novos usuários

## 🎨 Características Visuais

### Cores Modernas
- **Primária**: `#f39c12` (Laranja vibrante)
- **Secundária**: `#2c3e50` (Azul escuro)
- **Sucesso**: `#27ae60` (Verde)
- **Perigo**: `#e74c3c` (Vermelho)
- **Aviso**: `#f1c40f` (Amarelo)
- **Informação**: `#3498db` (Azul claro)

### Elementos de Design
- **Gradientes**: Backgrounds com gradientes suaves
- **Glassmorphism**: Efeito de vidro fosco nos cards
- **Animações**: Keyframes para fadeInUp, slideInLeft e pulse
- **Botões Modernos**: Com gradientes e efeitos de hover
- **Cards Animados**: Com sombras dinâmicas e transições

### Tipografia
- Fonte principal: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif
- Títulos com gradientes coloridos
- Textos com contraste otimizado

## 👤 Usuário Padrão
- Username: admin
- Senha: admin123

## 🛠️ Tecnologias Utilizadas

### Backend
- **Spring Boot**: Framework Java
- **Spring Security**: Segurança e autenticação
- **Spring Data JPA**: Persistência de dados
- **H2 Database**: Banco de dados em memória
- **JWT**: JSON Web Tokens para autenticação

### Frontend
- **Angular 17**: Framework moderno
- **Bootstrap 5**: CSS framework
- **Font Awesome**: Ícones vetoriais
- **CSS3**: Animações e gradientes
- **TypeScript**: Linguagem tipada

### Correções Recentes
- ✅ Corrigido erro de compilação no componente de registro
- ✅ Adicionado suporte à propriedade `localizacao` nos apiários
- ✅ Corrigido problema de visibilidade dos botões outline
- ✅ Implementado estilos modernos para todos os tipos de botões
 - ✅ Ajustado navegação dos botões "Voltar/Cancelar" ao criar Apiário para retornar a `/apiarios`
 - ✅ Botão "Voltar" na tela de Colmeias agora retorna ao Apiário (`/apiarios/:id`) quando há contexto, ou à lista de Apiários (`/apiarios`)
 - ✅ Tela de Colmeias aceita `apiarioId` como query param para pré‑seleção (`/colmeias?apiarioId=<id>`) 

## 📱 Responsividade
O sistema é totalmente responsivo e funciona perfeitamente em:
- Desktop (1920px+)
- Laptop (1024px - 1919px)
- Tablet (768px - 1023px)
- Mobile (até 767px)

## 🌙 Modo Escuro
Suporte automático ao modo escuro com:
- Backgrounds escuros com gradientes
- Textos claros com alto contraste
- Cards com transparência ajustada
- Manutenção da identidade visual

## 🔧 Instalação e Configuração

### Backend
```bash
# Clone o repositório
git clone [url-do-repositorio]

# Acesse a pasta backend
cd backend

# Compile e execute
mvn clean install
mvn spring-boot:run
```

#### Executar em modo desenvolvimento (sem PostgreSQL)
Para rodar rapidamente sem precisar do PostgreSQL local, use o perfil `dev`, que habilita um banco H2 em memória:

```bash
# A partir da raiz do projeto
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ou dentro da pasta backend
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Com o perfil `dev`:
- Banco H2 em memória é usado automaticamente.
- `spring.jpa.hibernate.ddl-auto=update` cria/atualiza as tabelas ao iniciar.
- Porta permanece `8080` e CORS já permite `http://localhost:4200`.

### Frontend
```bash
# Acesse a pasta frontend
cd frontend

# Instale as dependências
npm install

# Execute em modo desenvolvimento
npm start

# Ou compile para produção
npm run build
```

## 🧭 Rotas e Navegação

- `/:` Página inicial com atalhos para "Meus Apiários" e "Minhas Colmeias".
- `/dashboard`: Visão geral com estatísticas e ações rápidas (inclui atalho "Nova Colmeia").
- `/apiarios`: Lista e gestão de apiários.
- `/apiarios/novo`: Criação de novo apiário.
  - Botões "Voltar" e "Cancelar" retornam para `/apiarios`.
- `/apiarios/:id`: Página inicial do apiário selecionado.
- `/apiarios/:id/editar`: Edição de apiário.
- `/colmeias`: Gestão de colmeias (pode listar todas ou por apiário).
  - Suporta pré‑seleção via `apiarioId` como query param, ex.: `/colmeias?apiarioId=1`.
  - Botão "Voltar":
    - Com `apiarioId` (ou apiário selecionado): navega para `/apiarios/:id`.
    - Sem contexto: navega para `/apiarios`.

### Dicas de Navegação
- Para abrir diretamente as colmeias de um apiário específico, use `/colmeias?apiarioId=<id>`.
- Em listagens de apiários, utilize o atalho "Ver Colmeias" para contexto de navegação consistente.

## 📝 Notas de Desenvolvimento
- O sistema utiliza porta 8080 para backend e 4200 para frontend
- Banco H2 é iniciado automaticamente (dados em memória)
- JWT tokens têm validade configurável em `application.properties`
- Interface moderna com animações suaves e feedback visual
