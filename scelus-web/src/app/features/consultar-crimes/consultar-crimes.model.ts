export interface CrimeDTO {
  idFatoOcorrido: number;
  numeroProcesso: string;
  codigoAssunto: number;
  dataFato: string;
  medidaProtetiva: string;
  nomeVitima: string;
  cpfVitima: string;
  nomeAcusado: string;
  cpfAcusado: string;
  tipoVinculo: string;
  deficienciaVitima?: string;
}

export interface FiltroConsultaCrimes {
  numeroProcesso?: string;
  codigoAssunto?: number;
  nomeVitima?: string;
  cpfVitima?: string;
  nomeAcusado?: string;
  cpfAcusado?: string;
  tipoVinculo?: number;
  dataFato?: string;
  medidaProtetiva?: string;
  _offset?: number;
  _limit?: number;
}

/** Parte processual retornada pelo PJe. */
export interface PartePje {
  idParte: number;
  idProcesso: number;
  polo: string;
  nome: string;
  nomeSocial?: string;
  cpfCnpj?: string;
  dataNascimento?: string;
  genero?: string;
}

/** Processo retornado pelo PJe com suas partes. */
/** Assunto vinculado ao processo, retornado pelo PJe. */
export interface AssuntoPje {
  codigo: number;
  descricao: string;
}

export interface ProcessoPje {
  idProcesso: number;
  numeroUnico: string;
  possuiSentenca: string;
  partes: PartePje[];
  assuntos: AssuntoPje[];
}

export interface ParteCadastro {
  idParte: number;
  idPolo: number;
  idCep?: number;
  possuiAntecedentes?: number;
  reincidente?: number;
  observacaoAntecedentes?: string;
  idRacaEtnia?: number;
  idEstadoCivil?: number;
  idReligiao?: number;
  idEscolaridade?: number;
  idRenda?: number;
  idSituacaoUsoDroga?: number;
  idOcupacao?: number;
}

/** Payload de cadastro de crime (CSU002). */
export interface CadastroCrimeRequest {
  numeroProcesso: string;
  codigoAssunto: number;
  dataFato: string;
  idCep: number;
  medidaProtetiva: string;
  vitima: ParteCadastro;
  acusado: ParteCadastro;
  idTipoVinculo?: number;
  observacaoVinculo?: string;
}

/** Item genérico de tabela de domínio (id + descrição). */
export interface ItemDominio {
  id: number;
  descricao: string;
}

/** CEP/endereço para pesquisa e seleção. */
export interface CepDTO {
  id: number;
  cep: string;
  logradouro?: string;
  bairro?: string;
  municipio?: string;
  uf?: string;
}

/** Medida protetiva de urgência na listagem/seleção. */
export interface MpuDTO {
  id: number;
  numeroMpu?: string;
  numeroUnico?: string;
  legislacaoFundamento: string;
  dataDecisao: string;
  concedida: string;
  dataIntimacaoAcusado?: string;
  dataIntimacaoVitima?: string;
  dataCienciaVitima?: string;
  dataCienciaAcusado?: string;
  pedidoDesistencia?: string;
  inqueritoInstaurado?: string;
  observacoes?: string;
}

/** MPU vinculada a um fato ocorrido com justificativa. */
export interface MpuVinculada {
  id: number;
  idMpu: number;
  numeroMpu?: string;
  legislacaoFundamento: string;
  dataDecisao: string;
  concedida: string;
  justificativa?: string;
  observacaoJustificativa?: string;
  dataCriacao: string;
}

/** Payload de vínculo de MPU ao fato ocorrido (CSU008). */
export interface VinculoMpuRequest {
  idMpu: number;
  idJustificativaInclusaoMpu: number;
  observacaoJustificativa?: string;
}

// ── Wizard completo do CSU002 (POST /api/crimes/completo) ──────────────────

/** Benefício assistencial associado a uma parte (Tela 2.3). */
export interface BeneficioWizard {
  idTipoBeneficio: number;
  dataInicio?: string;
}

/** Configuração familiar declarada pela vítima (Tela 2.4). */
export interface ConfiguracaoFamiliarWizard {
  idTipoConfiguracaoFamiliar: number;
  dataDeclaracao?: string;
  observacao?: string;
}

/** Comunicante do fato ocorrido (Tela 2.8). */
export interface ComunicanteWizard {
  nome: string;
  telefone?: string;
  email?: string;
  cpfCnpj?: string;
  idTipoComunicante: number;
  dataDenuncia?: string;
  observacao?: string;
  anonimizado?: boolean;
}

/** Consequência da violência associada a uma parte (Tela 2.9). */
export interface ConsequenciaViolenciaWizard {
  chaveParte: string;
  idTipoConsequenciaViolencia: number;
  observacao: string;
}

/** Vínculo entre vítima e acusado no wizard (Tela 2.7). */
export interface VinculoWizard {
  chaveVitima: string;
  chaveAcusado: string;
  idTipoVinculo: number;
  observacao?: string;
}

/** Perfil demográfico completo de uma parte (vítima ou acusado) no wizard (Telas 2.5/2.6). */
export interface ParteWizard {
  chave: string;
  idParte: number;
  idPolo: number;
  idSituacaoUsoDroga?: number;
  idEstadoCivil?: number;
  idEscolaridade?: number;
  idRenda?: number;
  idReligiao?: number;
  idPosicaoProle?: number;
  idRacaEtnia?: number;
  observacoesPosicaoProle?: string;
  idsOcupacao?: number[];
  idsDeficiencia?: number[];
  idsDroga?: number[];
  idEscutaJudicial?: number;
  idCep?: number;
  beneficios?: BeneficioWizard[];
  configuracoesFamiliares?: ConfiguracaoFamiliarWizard[];
  possuiAntecedentes?: number;
  reincidente?: number;
  observacaoAntecedentes?: string;
}

/** Crime cometido no wizard (Tela 2.2) — assunto do processo tipificado. */
export interface CrimeCometidoWizard {
  codigoAssunto: number;
  dataInicioTipificacao: string;
  dataFimTipificacao?: string;
}

/** Fato ocorrido no wizard (Tela 2.8). */
export interface FatoOcorridoWizard {
  codigoAssunto: number;
  chaveVitima: string;
  chaveAcusado: string;
  dataFato: string;
  idCep: number;
  medidaProtetiva: string;
  comunicantes?: ComunicanteWizard[];
}

/** Vínculo de MPU nova ou existente no wizard (Tela 2.8 → CSU008). */
export interface MpuVinculoWizard {
  idMpuExistente?: number;
  novaMpu?: {
    legislacaoFundamento: string;
    dataDecisao: string;
    concedida: string;
    dataIntimacaoAcusado: string;
    dataIntimacaoVitima: string;
    dataCienciaVitima?: string;
    dataCienciaAcusado?: string;
    pedidoDesistencia: string;
    inqueritoInstaurado: string;
    observacoes?: string;
    numeroMpu?: string;
    numeroUnico?: string;
  };
  idJustificativaInclusaoMpu: number;
  observacaoJustificativa?: string;
}

/** Payload consolidado do wizard completo de cadastro de crimes (CSU002). */
export interface CadastroCrimeCompletoRequest {
  numeroProcesso: string;
  crimesCometidos: CrimeCometidoWizard[];
  vitimas: ParteWizard[];
  acusados: ParteWizard[];
  vinculos?: VinculoWizard[];
  fatoOcorrido: FatoOcorridoWizard;
  mpus?: MpuVinculoWizard[];
  consequenciasViolencia?: ConsequenciaViolenciaWizard[];
}

/** Resposta do cadastro completo do wizard. */
export interface CadastroCrimeCompletoResponse {
  idFatoOcorrido: number;
  idProcessoCrime: number;
  mensagem: string;
}
