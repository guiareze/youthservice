## 1. Banco de Dados

- [x] 1.1 Atualizar `src/main/resources/schema.sql`: coluna `idade` passa de `INTEGER` para `INTEGER NOT NULL`
- [x] 1.2 Atualizar `PessoaEntity` (`infrastructure/persistence`): campo `idade` passa a `@Column(nullable = false)`

## 2. Domínio

- [x] 2.1 Atualizar `Pessoa` (`domain/model`): adicionar validação de que `idade` não pode ser `null` (lançando `PessoaInvalidaException` com mensagem "A idade é obrigatória"), mantendo a validação existente de "diferente de zero"
- [x] 2.2 Atualizar Javadoc/comentários relevantes se existirem referências a "idade opcional" (encontrada e corrigida na anotação `@Operation` de `PessoaController`)

## 3. DTOs e Validação de Entrada

- [x] 3.1 Atualizar `CadastrarPessoaRequest`: adicionar `@NotNull(message = "A idade é obrigatória")` no campo `idade`
- [x] 3.2 Atualizar anotação `@Schema` de `idade` em `CadastrarPessoaRequest` para remover a indicação de campo opcional e indicar que é obrigatório

## 4. Testes Unitários

- [x] 4.1 Atualizar `PessoaTest`: remover/ajustar o teste `deveCriarPessoaSemInformarIdade` (que hoje espera sucesso) e adicionar teste `deveRejeitarIdadeAusente` esperando `PessoaInvalidaException`
- [x] 4.2 Atualizar `CadastrarPessoaUseCaseTest`: remover/ajustar `deveCadastrarPessoaSemInformarIdade` para refletir que a idade agora é obrigatória
- [x] 4.3 Revisar `PessoaMapperTest`: garantir que os cenários de teste usam sempre um valor de `idade` válido (não `null`), já que não é mais um caso de uso legítimo (já estava conforme, nenhuma alteração necessária)

## 5. Testes de Integração

- [x] 5.1 Atualizar `PessoaControllerIntegrationTest`: alterar `deveCadastrarPessoaSemInformarIdade` para `deveRejeitarCadastroSemInformarIdade`, esperando `PessoaInvalidaException` (ou o comportamento equivalente de rejeição) em vez de sucesso
- [x] 5.2 Garantir que os demais testes de integração que cadastram pessoas passem explicitamente um valor de `idade` válido (já estavam conforme, nenhuma alteração necessária)

## 6. Documentação

- [x] 6.1 Atualizar `docs/api-pessoas.md`: campo `idade` passa de "opcional" para "obrigatório" na seção de requisição do `POST /pessoas`
- [x] 6.2 Atualizar `docs/api-pessoas.md`: adicionar/ajustar a linha da tabela de erros para o cenário de `idade` ausente, se necessário
- [x] 6.3 Conferir que a documentação OpenAPI/Swagger gerada (via anotação `@Schema` atualizada) reflete a obrigatoriedade do campo

## 7. Validação Final

- [x] 7.1 Executar o build completo do projeto (`mvn clean install` ou equivalente) com sucesso, sem erros de compilação
- [x] 7.2 Executar todos os testes unitários e de integração com sucesso (33/33 testes passando)
- [x] 7.3 Validar manualmente: cadastro sem `idade` retorna 400 com mensagem clara; cadastro com `idade` válida continua funcionando normalmente
- [x] 7.4 Revisar código: mudança segue os mesmos padrões de validação, erro e arquitetura hexagonal já usados no restante do projeto
