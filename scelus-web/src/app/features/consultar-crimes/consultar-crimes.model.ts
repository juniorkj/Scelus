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
export interface ProcessoPje {
  idProcesso: number;
  numeroUnico: string;
  possuiSentenca: string;
  partes: PartePje[];
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
