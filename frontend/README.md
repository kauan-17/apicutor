# Frontend (Angular)

Aplicação Angular do projeto Apicutor. Foi gerada com [Angular CLI](https://github.com/angular/angular-cli) versão 20.3.2 e personalizada com rotas e navegação para gerenciamento de Apiários e Colmeias.

## Rotas principais

- `/` Home com atalhos para "Meus Apiários" e "Minhas Colmeias".
- `/dashboard` Estatísticas e ações rápidas (inclui atalho "Nova Colmeia").
- `/apiarios` Lista e gestão de apiários.
- `/apiarios/novo` Criação de novo apiário. Botões "Voltar" e "Cancelar" retornam para `/apiarios`.
- `/apiarios/:id` Página inicial do apiário.
- `/apiarios/:id/editar` Edição de apiário.
- `/colmeias` Gestão de colmeias; aceita `apiarioId` como query param para pré‑seleção, ex.: `/colmeias?apiarioId=1`.
  - Botão "Voltar" navega para `/apiarios/:id` quando há contexto de apiário, caso contrário vai para `/apiarios`.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

Alternatively, you can run `npm start`, which uses `ng serve` under the hood.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
