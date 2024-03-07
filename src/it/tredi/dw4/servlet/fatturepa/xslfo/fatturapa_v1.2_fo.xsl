<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:fo="http://www.w3.org/1999/XSL/Format"
		xmlns:p="http://www.fatturapa.gov.it/sdi/fatturapa/v1.1"
		version="1.0">

	<xsl:output encoding="UTF-8" indent="yes" method="xml" standalone="no" omit-xml-declaration="no" />
	
	<xsl:template name="FormatDate">
		<xsl:param name="DateTime" />

		<xsl:variable name="year" select="substring($DateTime,1,4)" />
		<xsl:variable name="month" select="substring($DateTime,6,2)" />
		<xsl:variable name="day" select="substring($DateTime,9,2)" />

		<xsl:value-of select="$day" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$month" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$year" />
	</xsl:template>
	
	<xsl:template name="FormatDateYYYYMMDD">
		<xsl:param name="DateTime" />

		<xsl:variable name="year" select="substring($DateTime,1,4)" />
		<xsl:variable name="month" select="substring($DateTime,5,2)" />
		<xsl:variable name="day" select="substring($DateTime,7,2)" />

		<xsl:value-of select="$day" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$month" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$year" />
	</xsl:template>
	
	<xsl:template name="FormatNumProt">
		<xsl:param name="ExtendeNumProt" />

		<xsl:variable name="year" select="substring($ExtendeNumProt,1,4)" />
		<xsl:variable name="number" select="number(substring($ExtendeNumProt,14))" />

		<xsl:value-of select="$number" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$year" />
	</xsl:template>
	
	<xsl:attribute-set name="h5">
		<xsl:attribute name="font-size">16pt</xsl:attribute>
		<xsl:attribute name="line-height">19pt</xsl:attribute>
		<xsl:attribute name="space-after">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="h6">
		<xsl:attribute name="font-size">14pt</xsl:attribute>
		<xsl:attribute name="line-height">16pt</xsl:attribute>
		<xsl:attribute name="space-after">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="section-title">
		<xsl:attribute name="font-size">8pt</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="space-after">8pt</xsl:attribute>
		<xsl:attribute name="space-before">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="padding-top-20">
		<xsl:attribute name="space-before">20pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="padding-top-10">
		<xsl:attribute name="space-before">10pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="padding-top-5">
		<xsl:attribute name="space-before">5pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-size-8">
		<xsl:attribute name="font-size">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="font-size-10">
		<xsl:attribute name="font-size">10pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="table-content">
		<xsl:attribute name="table-layout">fixed</xsl:attribute>
		<xsl:attribute name="width">100%</xsl:attribute>
		<xsl:attribute name="space-after">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="layout-table">
		<xsl:attribute name="table-layout">fixed</xsl:attribute>
		<xsl:attribute name="width">100%</xsl:attribute>
		<xsl:attribute name="space-after">12pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="cell-padding">
		<xsl:attribute name="padding">0.8mm</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="cell-header">
		<xsl:attribute name="background-color">#ccc</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="border">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="border-width">0.1mm</xsl:attribute>
		<xsl:attribute name="border-style">solid</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="border-strong">
		<xsl:attribute name="border-color">black</xsl:attribute>
		<xsl:attribute name="border-width">0.3mm</xsl:attribute>
		<xsl:attribute name="border-style">solid</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="table-cell-label-small">
		<xsl:attribute name="font-size">6pt</xsl:attribute>
		<xsl:attribute name="space-after">6pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="cedente-prestatore">
		<xsl:attribute name="font-size">12pt</xsl:attribute>
		<xsl:attribute name="text-align">right</xsl:attribute>
		<xsl:attribute name="space-after">8pt</xsl:attribute>
		<xsl:attribute name="space-before">20pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="cedente-prestatore-text">
		<xsl:attribute name="font-size">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="cessionario-committente">
		<xsl:attribute name="font-size">12pt</xsl:attribute>
		<xsl:attribute name="space-after">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="cessionario-committente-text">
		<xsl:attribute name="font-size">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="table-protocollo">
		<xsl:attribute name="table-layout">fixed</xsl:attribute>
		<xsl:attribute name="width">75%</xsl:attribute>
		<xsl:attribute name="space-after">50pt</xsl:attribute>
		<xsl:attribute name="font-size">10pt</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="border-protocollo">
		<xsl:attribute name="border-color">red</xsl:attribute>
		<xsl:attribute name="border-width">0.3mm</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:template match="/">
		<fo:root language="IT">
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4-portrail" page-height="297mm" page-width="210mm"
																					margin-top="5mm" margin-bottom="5mm" margin-left="5mm"
																					margin-right="5mm">
					
					<fo:region-body margin-top="25mm" margin-bottom="20mm" />
					<fo:region-before region-name="xsl-region-before" extent="25mm" display-align="before" precedence="true" />
					<fo:region-after region-name="xsl-region-after" extent="15mm" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="A4-portrail">
				
				<!-- INIZIO HEADER DI OGNI PAGINA DELLA FATTURA -->
				<fo:static-content flow-name="xsl-region-before">
					<fo:table table-layout="fixed" width="100%" border-width="0" font-size="8pt">
						<fo:table-column column-width="proportional-column-width(80)" />
						<fo:table-column column-width="proportional-column-width(20)" />
						
						<fo:table-body>
							<fo:table-row keep-together.within-page="always">
								<fo:table-cell text-align="left">
									<fo:block>
										FATTURA ELETTRONICA
										<xsl:if test="/*[local-name()='FatturaElettronica']/@versione">
											ver <xsl:value-of select="/*[local-name()='FatturaElettronica']/@versione"/>
										</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right">
									<fo:block>Pagina <fo:page-number /></fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				<!-- FINE HEADER DI OGNI PAGINA DELLA FATTURA -->
				
				<!-- INIZIO FOOTER DI OGNI PAGINA DELLA FATTURA -->
				<!--fo:static-content flow-name="xsl-region-after">
					<fo:block font-size="8pt" text-align="center">- <fo:page-number /> -</fo:block>
				</fo:static-content-->
				<!-- FINE FOOTER DI OGNI PAGINA DELLA FATTURA -->
			
				<!-- INZIO CORPO DI OGNI PAGINA DELLA FATTURA -->
				<fo:flow flow-name="xsl-region-body" border-collapse="collapse" reference-orientation="0">
				
					<!--INIZIO SEGNATURA DI PROTOCOLLO-->
					<xsl:if test="/*[local-name()='FatturaElettronica']/DatiSegnatura/NumeroProtocollo and /*[local-name()='FatturaElettronica']/DatiSegnatura/NumeroProtocollo != ''">
						<fo:table xsl:use-attribute-sets="table-protocollo">
							<fo:table-column column-width="proportional-column-width(34)" />
							<fo:table-column column-width="proportional-column-width(33)" />
							<fo:table-column column-width="proportional-column-width(33)" />
							
							<fo:table-body>
								<fo:table-row keep-together.within-page="always">
									<fo:table-cell xsl:use-attribute-sets="cell-padding border border-protocollo">
										<fo:block xsl:use-attribute-sets="table-cell-label-small">
											Numero Protocollo:
										</fo:block>
										<fo:block>
											<xsl:text>N. </xsl:text>
											<xsl:call-template name="FormatNumProt">
												<xsl:with-param name="ExtendeNumProt" select="/*[local-name()='FatturaElettronica']/DatiSegnatura/NumeroProtocollo" />
											</xsl:call-template>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="cell-padding border border-protocollo">
										<fo:block xsl:use-attribute-sets="table-cell-label-small">
											Data Protocollo:
										</fo:block>
										<fo:block>
											<xsl:if test="/*[local-name()='FatturaElettronica']/DatiSegnatura/DataProtocollo">
												<xsl:choose>
													<xsl:when test="string-length(/*[local-name()='FatturaElettronica']/DatiSegnatura/DataProtocollo) = 8">
														<xsl:call-template name="FormatDateYYYYMMDD">
															<xsl:with-param name="DateTime" select="/*[local-name()='FatturaElettronica']/DatiSegnatura/DataProtocollo" />
														</xsl:call-template>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="/*[local-name()='FatturaElettronica']/DatiSegnatura/DataProtocollo" />
													</xsl:otherwise>
												</xsl:choose>
											</xsl:if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell xsl:use-attribute-sets="cell-padding border border-protocollo">
										<fo:block xsl:use-attribute-sets="table-cell-label-small">
											Tipo Documento:
										</fo:block>
										<fo:block text-transform="uppercase">
											<xsl:value-of select="/*[local-name()='FatturaElettronica']/DatiSegnatura/TipoDocumento" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<xsl:if test="/*[local-name()='FatturaElettronica']/DatiSegnatura/ClassificazioneDocumento">
									<fo:table-row keep-together.within-page="always">
										<fo:table-cell xsl:use-attribute-sets="cell-padding border border-protocollo" number-columns-spanned="3">
											<fo:block xsl:use-attribute-sets="table-cell-label-small">
												Classificazione:
											</fo:block>
											<fo:block>
												<xsl:value-of select="/*[local-name()='FatturaElettronica']/DatiSegnatura/ClassificazioneDocumento" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</xsl:if>
							</fo:table-body>
						</fo:table>
					</xsl:if>
					<!--FINE SEGNATURA DI PROTOCOLLO-->
					
					<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader">
						
						<!--INIZIO DATI DELLA TRASMISSIONE-->
						<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/DatiTrasmissione">
							<fo:block xsl:use-attribute-sets="h6">
								Dati relativi alla trasmissione
							</fo:block>
							
							<xsl:for-each select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/DatiTrasmissione">
								<fo:table xsl:use-attribute-sets="table-content border font-size-8">
									<fo:table-column column-width="proportional-column-width(100)" />
									<fo:table-body>
										<fo:table-row keep-together.within-page="always">
											<fo:table-cell xsl:use-attribute-sets="cell-padding">
												<xsl:if test="IdTrasmittente">
													<fo:block>
														Identificativo del trasmittente:
														<fo:inline font-weight="bold">
															<xsl:value-of select="IdTrasmittente/IdPaese" />
															<xsl:value-of select="IdTrasmittente/IdCodice" />
														</fo:inline>
													</fo:block>
												</xsl:if>
												<xsl:if test="ProgressivoInvio">
													<fo:block>
														Progressivo di invio:
														<fo:inline font-weight="bold">
															<xsl:value-of select="ProgressivoInvio" />
														</fo:inline>
													</fo:block>
												</xsl:if>
												<xsl:if test="FormatoTrasmissione">
													<fo:block>
														Formato Trasmissione:
														<fo:inline font-weight="bold">
															<xsl:value-of select="FormatoTrasmissione" />
														</fo:inline>
													</fo:block>
												</xsl:if>
												<xsl:if test="CodiceDestinatario">
													<fo:block>
														Codice Amministrazione destinataria:
														<fo:inline font-weight="bold">
															<xsl:value-of select="CodiceDestinatario" />
														</fo:inline>
													</fo:block>
												</xsl:if>
												<xsl:if test="ContattiTrasmittente/Telefono">
													<fo:block>
														Telefono del trasmittente:
														<fo:inline font-weight="bold">
															<xsl:value-of select="ContattiTrasmittente/Telefono" />
														</fo:inline>
													</fo:block>
												</xsl:if>
												<xsl:if test="ContattiTrasmittente/Email">
													<fo:block>
														E-mail del trasmittente:
														<fo:inline font-weight="bold">
															<xsl:value-of select="ContattiTrasmittente/Email" />
														</fo:inline>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>	
							</xsl:for-each>
						</xsl:if>
						<!--FINE DATI DELLA TRASMISSIONE-->
						
						<!--INIZIO DATI CEDENTE PRESTATORE-->
						<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore">
							<fo:block xsl:use-attribute-sets="cedente-prestatore">
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione">
									<fo:block font-weight="bold">
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione" />
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Cognome">
									<fo:block font-weight="bold">
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Cognome" />
										<xsl:text> </xsl:text>
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Nome" />
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA">
									<fo:block xsl:use-attribute-sets="cedente-prestatore-text">
										P.IVA: 
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdPaese" />
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdCodice" />
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/CodiceFiscale">
									<fo:block xsl:use-attribute-sets="cedente-prestatore-text">
										C.F.: <xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/CodiceFiscale" />
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo">
									<fo:block xsl:use-attribute-sets="cedente-prestatore-text">
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo" />
										<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/NumeroCivico">
											<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/NumeroCivico" />
										</xsl:if>
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Comune">
									<fo:block xsl:use-attribute-sets="cedente-prestatore-text">
										<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/CAP">
											<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/CAP" />
											<xsl:text> </xsl:text>
										</xsl:if>
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Comune" />
										<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Provincia">
											<xsl:text> (</xsl:text>
											<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Provincia" />
											<xsl:text>)</xsl:text>
										</xsl:if>
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Nazione">
									<fo:block xsl:use-attribute-sets="cedente-prestatore-text">
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Nazione" />
									</fo:block>
								</xsl:if>
							</fo:block>
						</xsl:if>
						<!--FINE DATI CEDENTE PRESTATORE-->
						
						<!--INIZIO DATI CESSIONARIO COMMITTENTE-->
						<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente">
							<fo:block xsl:use-attribute-sets="cessionario-committente">
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Denominazione">
									<fo:block font-weight="bold">
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Denominazione" />
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Cognome">
									<fo:block font-weight="bold">
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Cognome" />
										<xsl:text> </xsl:text>
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Nome" />
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA">
									<fo:block xsl:use-attribute-sets="cessionario-committente-text">
										P.IVA: 
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdPaese" />
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdCodice" />
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/CodiceFiscale">
									<fo:block xsl:use-attribute-sets="cessionario-committente-text">
										C.F.: <xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/CodiceFiscale" />
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Indirizzo">
									<fo:block xsl:use-attribute-sets="cessionario-committente-text">
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Indirizzo" />
										<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/NumeroCivico">
											<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/NumeroCivico" />
										</xsl:if>
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Comune">
									<fo:block xsl:use-attribute-sets="cessionario-committente-text">
										<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/CAP">
											<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/CAP" />
											<xsl:text> </xsl:text>
										</xsl:if>
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Comune" />
										<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Provincia">
											<xsl:text> (</xsl:text>
											<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Provincia" />
											<xsl:text>)</xsl:text>
										</xsl:if>
									</fo:block>
								</xsl:if>
								<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Nazione">
									<fo:block xsl:use-attribute-sets="cessionario-committente-text">
										<xsl:value-of select="/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Nazione" />
									</fo:block>
								</xsl:if>
							</fo:block>
						</xsl:if>
						<!--FINE DATI CESSIONARIO COMMITTENTE-->
						
						<xsl:if test="/*[local-name()='FatturaElettronica']/FatturaElettronicaBody">
							<fo:block xsl:use-attribute-sets="padding-top-20">
								<xsl:if test="count(/*[local-name()='FatturaElettronica']/FatturaElettronicaBody) > 1">
									<fo:block xsl:use-attribute-sets="h5">Lotto di Fatture</fo:block>
								</xsl:if>
								<xsl:for-each select="/*[local-name()='FatturaElettronica']/FatturaElettronicaBody">
									<xsl:if test="count(/*[local-name()='FatturaElettronica']/FatturaElettronicaBody) > 1">
										<fo:block xsl:use-attribute-sets="h6 padding-top-10">Documento num. <xsl:value-of select="position()" /> del Lotto</fo:block>
									</xsl:if>
									
									<!--INIZIO DATI DELL'ORDINE DI ACQUISTO-->
									<xsl:if test="DatiGenerali/DatiOrdineAcquisto">
										<fo:block xsl:use-attribute-sets="section-title">Ordine di acquisto</fo:block>
										
										<fo:table xsl:use-attribute-sets="table-content font-size-8">
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(16)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-header>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea in fattura</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Identificativo ordine</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Data ordine</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea ordine</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Codice commessa/convenzione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CUP</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CIG</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-header>
											<fo:table-body>
												<xsl:for-each select="DatiGenerali/DatiOrdineAcquisto">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="RiferimentoNumeroLinea">
																	<xsl:for-each select="RiferimentoNumeroLinea">
																		<xsl:if test="(position( )) > 1"><xsl:text>, </xsl:text></xsl:if>
																		<xsl:value-of select="." />
																	</xsl:for-each>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="IdDocumento">
																	<xsl:value-of select="IdDocumento" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="Data">
																	<xsl:call-template name="FormatDate">
																		<xsl:with-param name="DateTime" select="Data" />
																	</xsl:call-template>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="NumItem">
																	<xsl:value-of select="NumItem" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCommessaConvenzione">
																	<xsl:value-of select="CodiceCommessaConvenzione" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCUP">
																	<xsl:value-of select="CodiceCUP" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCIG">
																	<xsl:value-of select="CodiceCIG" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI DELL'ORDINE DI ACQUISTO-->
									
									<!--INIZIO DATI DEL CONTRATTO-->
									<xsl:if test="DatiGenerali/DatiContratto">
										<fo:block xsl:use-attribute-sets="section-title">Contratto</fo:block>
										
										<fo:table xsl:use-attribute-sets="table-content font-size-8">
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(16)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-header>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea in fattura</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Identificativo contratto</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Data contratto</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea contratto</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Codice commessa/convenzione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CUP</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CIG</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-header>
											<fo:table-body>
												<xsl:for-each select="DatiGenerali/DatiContratto">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="RiferimentoNumeroLinea">
																	<xsl:for-each select="RiferimentoNumeroLinea">
																		<xsl:if test="(position( )) > 1"><xsl:text>, </xsl:text></xsl:if>
																		<xsl:value-of select="." />
																	</xsl:for-each>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="IdDocumento">
																	<xsl:value-of select="IdDocumento" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="Data">
																	<xsl:call-template name="FormatDate">
																		<xsl:with-param name="DateTime" select="Data" />
																	</xsl:call-template>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="NumItem">
																	<xsl:value-of select="NumItem" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCommessaConvenzione">
																	<xsl:value-of select="CodiceCommessaConvenzione" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCUP">
																	<xsl:value-of select="CodiceCUP" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCIG">
																	<xsl:value-of select="CodiceCIG" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI DEL CONTRATTO-->
									
									<!--INIZIO DATI CONVENZIONE-->
									<xsl:if test="DatiGenerali/DatiConvenzione">
										<fo:block xsl:use-attribute-sets="section-title">Convenzione</fo:block>
										
										<fo:table xsl:use-attribute-sets="table-content font-size-8">
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(16)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-header>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea in fattura</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Identificativo convenzione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Data convenzione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea convenzione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Codice commessa/convenzione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CUP</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CIG</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-header>
											<fo:table-body>
												<xsl:for-each select="DatiGenerali/DatiConvenzione">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="RiferimentoNumeroLinea">
																	<xsl:for-each select="RiferimentoNumeroLinea">
																		<xsl:if test="(position( )) > 1"><xsl:text>, </xsl:text></xsl:if>
																		<xsl:value-of select="." />
																	</xsl:for-each>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="IdDocumento">
																	<xsl:value-of select="IdDocumento" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="Data">
																	<xsl:call-template name="FormatDate">
																		<xsl:with-param name="DateTime" select="Data" />
																	</xsl:call-template>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="NumItem">
																	<xsl:value-of select="NumItem" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCommessaConvenzione">
																	<xsl:value-of select="CodiceCommessaConvenzione" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCUP">
																	<xsl:value-of select="CodiceCUP" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCIG">
																	<xsl:value-of select="CodiceCIG" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI CONVENZIONE-->
									
									<!--INIZIO DATI RICEZIONE-->
									<xsl:if test="DatiGenerali/DatiRicezione">
										<fo:block xsl:use-attribute-sets="section-title">Ricezione</fo:block>
										
										<fo:table xsl:use-attribute-sets="table-content font-size-8">
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(16)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-header>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea in fattura</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Identificativo ricezione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Data ricezione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea ricezione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Codice commessa/convenzione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CUP</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CIG</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-header>
											<fo:table-body>
												<xsl:for-each select="DatiGenerali/DatiRicezione">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="RiferimentoNumeroLinea">
																	<xsl:for-each select="RiferimentoNumeroLinea">
																		<xsl:if test="(position( )) > 1"><xsl:text>, </xsl:text></xsl:if>
																		<xsl:value-of select="." />
																	</xsl:for-each>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="IdDocumento">
																	<xsl:value-of select="IdDocumento" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="Data">
																	<xsl:call-template name="FormatDate">
																		<xsl:with-param name="DateTime" select="Data" />
																	</xsl:call-template>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="NumItem">
																	<xsl:value-of select="NumItem" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCommessaConvenzione">
																	<xsl:value-of select="CodiceCommessaConvenzione" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCUP">
																	<xsl:value-of select="CodiceCUP" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCIG">
																	<xsl:value-of select="CodiceCIG" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI RICEZIONE-->
									
									<!--INIZIO DATI FATTURE COLLEGATE-->
									<xsl:if test="DatiGenerali/DatiFattureCollegate">
										<fo:block xsl:use-attribute-sets="section-title">Fatture collegate</fo:block>
										
										<fo:table xsl:use-attribute-sets="table-content font-size-8">
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(16)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-column column-width="proportional-column-width(14)" />
											<fo:table-header>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea in fattura</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Identificativo fattura collegata</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Data fattura collegata</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Numero linea fattura collegata</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Codice commessa/convenzione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CUP</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>CIG</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-header>
											<fo:table-body>
												<xsl:for-each select="DatiGenerali/DatiFattureCollegate">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="RiferimentoNumeroLinea">
																	<xsl:for-each select="RiferimentoNumeroLinea">
																		<xsl:if test="(position( )) > 1"><xsl:text>, </xsl:text></xsl:if>
																		<xsl:value-of select="." />
																	</xsl:for-each>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="IdDocumento">
																	<xsl:value-of select="IdDocumento" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="Data">
																	<xsl:call-template name="FormatDate">
																		<xsl:with-param name="DateTime" select="Data" />
																	</xsl:call-template>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="NumItem">
																	<xsl:value-of select="NumItem" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCommessaConvenzione">
																	<xsl:value-of select="CodiceCommessaConvenzione" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCUP">
																	<xsl:value-of select="CodiceCUP" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="CodiceCIG">
																	<xsl:value-of select="CodiceCIG" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI FATTURE COLLEGATE-->
									
									<!--INIZIO DATI RIFERIMENTO SAL-->
									<xsl:if test="DatiGenerali/DatiSAL">
										<fo:table xsl:use-attribute-sets="table-content border-strong font-size-10">
											<fo:table-column column-width="proportional-column-width(100)" />
											<fo:table-body>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-padding border">
														<fo:block xsl:use-attribute-sets="table-cell-label-small">
															Stato Avanzamento Lavori:
														</fo:block>
														<fo:block>
															<xsl:if test="DatiGenerali/DatiSAL/RiferimentoFase">
																<xsl:for-each select="DatiGenerali/DatiSAL/RiferimentoFase">
																	<xsl:if test="(position( )) > 1"><xsl:text>, </xsl:text></xsl:if>
																	<xsl:value-of select="." />
																</xsl:for-each>
															</xsl:if>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI RIFERIMENTO SAL-->
									
									<!--INIZIO DATI GENERALI DOCUMENTO-->									
									<xsl:if test="DatiGenerali/DatiGeneraliDocumento">
										<fo:table xsl:use-attribute-sets="padding-top-20 table-content font-size-10">
											<fo:table-column column-width="proportional-column-width(30)" />
											<fo:table-column column-width="proportional-column-width(20)" />
											<fo:table-column column-width="proportional-column-width(25)" />
											<fo:table-column column-width="proportional-column-width(25)" />
											<fo:table-body>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-padding border">
														<fo:block xsl:use-attribute-sets="table-cell-label-small">
															Tipologia documento:
														</fo:block>
														<xsl:if test="DatiGenerali/DatiGeneraliDocumento/TipoDocumento">
															<fo:block font-weight="bold">
																<xsl:variable name="TD">
																	<xsl:value-of select="DatiGenerali/DatiGeneraliDocumento/TipoDocumento" />
																</xsl:variable>
																<xsl:choose>
																	<xsl:when test="$TD='TD01'">
																		Fattura
																	</xsl:when>
																	<xsl:when test="$TD='TD02'">
																		Acconto/Anticipo su fattura
																	</xsl:when>
																	<xsl:when test="$TD='TD03'">
																		Acconto/Anticipo su parcella
																	</xsl:when>
																	<xsl:when test="$TD='TD04'">
																		Nota di credito
																	</xsl:when>
																	<xsl:when test="$TD='TD05'">
																		Nota di debito
																	</xsl:when>
																	<xsl:when test="$TD='TD06'">
																		Parcella
																	</xsl:when>
																</xsl:choose>
															</fo:block>
														</xsl:if>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-padding border">
														<fo:block xsl:use-attribute-sets="table-cell-label-small">
															Valuta:
														</fo:block>
														<xsl:if test="DatiGenerali/DatiGeneraliDocumento/Divisa">
															<fo:block>
																<xsl:value-of select="DatiGenerali/DatiGeneraliDocumento/Divisa" />
															</fo:block>
														</xsl:if>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-padding border">
														<fo:block xsl:use-attribute-sets="table-cell-label-small">
															Data documento:
														</fo:block>
														<xsl:if test="DatiGenerali/DatiGeneraliDocumento/Data">
															<fo:block font-weight="bold">
																<xsl:call-template name="FormatDate">
																	<xsl:with-param name="DateTime" select="DatiGenerali/DatiGeneraliDocumento/Data" />
																</xsl:call-template>
															</fo:block>
														</xsl:if>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-padding border">
														<fo:block xsl:use-attribute-sets="table-cell-label-small">
															Numero documento:
														</fo:block>
														<xsl:if test="DatiGenerali/DatiGeneraliDocumento/Numero">
															<fo:block font-weight="bold">
																<xsl:value-of select="DatiGenerali/DatiGeneraliDocumento/Numero" />
															</fo:block>
														</xsl:if>
													</fo:table-cell>
												</fo:table-row>
												<xsl:if test="DatiGenerali/DatiGeneraliDocumento/ImportoTotaleDocumento or DatiGenerali/DatiGeneraliDocumento/Arrotondamento">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Importo totale documento:
															</fo:block>
															<xsl:if test="DatiGenerali/DatiGeneraliDocumento/ImportoTotaleDocumento">
																<fo:block font-weight="bold">
																	<xsl:value-of select="DatiGenerali/DatiGeneraliDocumento/ImportoTotaleDocumento" />
																</fo:block>
															</xsl:if>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Arrotondamento su Importo totale:
															</fo:block>
															<xsl:if test="DatiGenerali/DatiGeneraliDocumento/Arrotondamento">
																<fo:block>
																	<xsl:value-of select="DatiGenerali/DatiGeneraliDocumento/Arrotondamento" />
																</fo:block>
															</xsl:if>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Art. 73 DPR 633/72:
															</fo:block>
															<xsl:if test="DatiGenerali/DatiGeneraliDocumento/Art73">
																<fo:block>
																	<xsl:value-of select="DatiGenerali/DatiGeneraliDocumento/Art73" />
																</fo:block>
															</xsl:if>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="4">
														<fo:block xsl:use-attribute-sets="table-cell-label-small">
															Causale:
														</fo:block>
														<fo:block>
															<xsl:for-each select="DatiGenerali/DatiGeneraliDocumento/Causale">
																<xsl:value-of select="current()" />
															</xsl:for-each>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI GENERALI DOCUMENTO-->
									
									<!--INIZIO DATI DELLA RITENUTA-->
									<xsl:if test="DatiGenerali/DatiGeneraliDocumento/DatiRitenuta">
										<fo:table xsl:use-attribute-sets="padding-top-20 table-content font-size-10">
											<fo:table-column column-width="proportional-column-width(34)" />
											<fo:table-column column-width="proportional-column-width(33)" />
											<fo:table-column column-width="proportional-column-width(33)" />
											<fo:table-body>
												<xsl:for-each select="DatiGenerali/DatiGeneraliDocumento/DatiRitenuta">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Ritenuta:
															</fo:block>
															<fo:block>
																<xsl:if test="TipoRitenuta">
																	<xsl:variable name="TR">
																		<xsl:value-of select="TipoRitenuta" />
																	</xsl:variable>
																	<xsl:choose>
																		<xsl:when test="$TR='RT01'">
																			Ritenuta persone fisiche
																		</xsl:when>
																		<xsl:when test="$TR='RT02'">
																			Ritenuta persone giuridiche
																		</xsl:when>
																	</xsl:choose>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Importo ritenuta:
															</fo:block>
															<fo:block>
																<xsl:if test="ImportoRitenuta">
																	<xsl:value-of select="ImportoRitenuta" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Aliquota ritenuta (%):
															</fo:block>
															<fo:block>
																<xsl:if test="AliquotaRitenuta">
																	<xsl:value-of select="AliquotaRitenuta" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:if test="CausalePagamento">
														<fo:table-row keep-together.within-page="always">
															<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="3">
																<fo:block xsl:use-attribute-sets="table-cell-label-small">
																	Causale di pagamento:
																</fo:block>
																<fo:block>
																	<xsl:value-of select="CausalePagamento" />
																	<xsl:if test="CausalePagamento != ''"> (decodifica come da modello 770S)</xsl:if>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI DELLA RITENUTA-->
									
									<!--INIZIO DATI DEL BOLLO-->
									<xsl:if test="DatiGenerali/DatiGeneraliDocumento/DatiBollo">
										<fo:table xsl:use-attribute-sets="padding-top-20 table-content font-size-10">
											<fo:table-column column-width="proportional-column-width(50)" />
											<fo:table-column column-width="proportional-column-width(50)" />
											<fo:table-body>
												<xsl:for-each select="DatiGenerali/DatiGeneraliDocumento/DatiBollo">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Bollo virtuale:
															</fo:block>
															<fo:block>
																<xsl:value-of select="BolloVirtuale" />
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Importo bollo:
															</fo:block>
															<fo:block>
																<xsl:value-of select="ImportoBollo" />
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI DEL BOLLO-->
									
									<!--INIZIO DATI DELLA CASSA PREVIDENZIALE-->
									<xsl:if test="DatiGenerali/DatiGeneraliDocumento/DatiCassaPrevidenziale">
										<xsl:for-each select="DatiGenerali/DatiGeneraliDocumento/DatiCassaPrevidenziale">
											<fo:table xsl:use-attribute-sets="table-content border-strong font-size-10">
												<xsl:if test="position() > 0">
													<xsl:attribute name="space-after">0pt</xsl:attribute>
												</xsl:if>
												<xsl:if test="position() != count(DatiGenerali/DatiGeneraliDocumento/DatiCassaPrevidenziale)">
													<xsl:attribute name="space-before">0pt</xsl:attribute>
												</xsl:if>
												<fo:table-column column-width="proportional-column-width(25)" />
												<fo:table-column column-width="proportional-column-width(25)" />
												<fo:table-column column-width="proportional-column-width(25)" />
												<fo:table-column column-width="proportional-column-width(25)" />
												<fo:table-body>
													<fo:table-row keep-together.within-page="always" number-columns-spanned="4">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Cassa previdenziale:
															</fo:block>
															<fo:block>
																<xsl:if test="TipoCassa">
																	<xsl:choose>
																		<xsl:when test="TipoCassa='TC01'">
																			Cassa Nazionale Previdenza e Assistenza Avvocati e Procuratori legali
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC02'">
																			Cassa Previdenza Dottori Commercialisti
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC03'">
																			Cassa Previdenza e Assistenza Geometri
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC04'">
																			Cassa Nazionale Previdenza e Assistenza Ingegneri e Architetti liberi profess.
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC05'">
																			Cassa Nazionale del Notariato
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC06'">
																			Cassa Nazionale Previdenza e Assistenza Ragionieri e Periti commerciali
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC07'">
																			Ente Nazionale Assistenza Agenti e Rappresentanti di Commercio-ENASARCO
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC08'">
																			Ente Nazionale Previdenza e Assistenza Consulenti del Lavoro-ENPACL
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC09'">
																			Ente Nazionale Previdenza e Assistenza Medici-ENPAM
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC10'">
																			Ente Nazionale Previdenza e Assistenza Farmacisti-ENPAF
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC11'">
																			Ente Nazionale Previdenza e Assistenza Veterinari-ENPAV
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC12'">
																			Ente Nazionale Previdenza e Assistenza Impiegati dell'Agricoltura-ENPAIA
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC13'">
																			Fondo Previdenza Impiegati Imprese di Spedizione e Agenzie Marittime
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC14'">
																			Istituto Nazionale Previdenza Giornalisti Italiani-INPGI
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC15'">
																			Opera Nazionale Assistenza Orfani Sanitari Italiani-ONAOSI
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC16'">
																			Cassa Autonoma Assistenza Integrativa Giornalisti Italiani-CASAGIT
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC17'">
																			Ente Previdenza Periti Industriali e Periti Industriali Laureati-EPPI
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC18'">
																			Ente Previdenza e Assistenza Pluricategoriale-EPAP
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC19'">
																			Ente Nazionale Previdenza e Assistenza Biologi-ENPAB
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC20'">
																			Ente Nazionale Previdenza e Assistenza Professione Infermieristica-ENPAPI
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC21'">
																			Ente Nazionale Previdenza e Assistenza Psicologi-ENPAP
																		</xsl:when>
																		<xsl:when test="TipoCassa='TC22'">
																			INPS
																		</xsl:when>
																	</xsl:choose>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Aliquota contributo cassa (%):
															</fo:block>
															<fo:block>
																<xsl:if test="AlCassa">
																	<xsl:value-of select="AlCassa" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Importo contributo cassa:
															</fo:block>
															<fo:block>
																<xsl:if test="ImportoContributoCassa">
																	<xsl:value-of select="ImportoContributoCassa" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Imponibile previdenziale:
															</fo:block>
															<fo:block>
																<xsl:if test="ImponibileCassa">
																	<xsl:value-of select="ImponibileCassa" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Aliquota IVA applicata:
															</fo:block>
															<fo:block>
																<xsl:if test="AliquotaIVA">
																	<xsl:value-of select="AliquotaIVA" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Contributo cassa soggetto a ritenuta:
															</fo:block>
															<fo:block>
																<xsl:if test="Ritenuta">
																	<xsl:value-of select="Ritenuta" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Tipologia di non imponibilità del contributo:
															</fo:block>
															<fo:block>
																<xsl:if test="Natura">
																	<xsl:choose>
																		<xsl:when test="Natura='N1'">
																			Escluse ex art. 15
																		</xsl:when>
																		<xsl:when test="Natura='N2'">
																			Non soggette
																		</xsl:when>
																		<xsl:when test="Natura='N3'">
																			Non imponibili
																		</xsl:when>
																		<xsl:when test="Natura='N4'">
																			Esenti
																		</xsl:when>
																		<xsl:when test="Natura='N5'">
																			Regime del margine
																		</xsl:when>
																		<xsl:when test="Natura='N6'">
																			Inversione contabile
																		</xsl:when>
																		<xsl:when test="Natura='N7'">
																			IVA assolta in altro stato UE
																		</xsl:when>
																	</xsl:choose>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Riferimento amministrativo / contabile:
															</fo:block>
															<fo:block>
																<xsl:if test="RiferimentoAmministrazione">
																	<xsl:value-of select="RiferimentoAmministrazione" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</xsl:for-each>
									</xsl:if>
									<!--FINE DATI DELLA CASSA PREVIDENZIALE-->
									
									<!--INIZIO DATI SCONTO / MAGGIORAZIONE-->
									<xsl:if test="DatiGenerali/DatiGeneraliDocumento/ScontoMaggiorazione">
										<fo:table xsl:use-attribute-sets="padding-top-20 table-content font-size-10">
											<fo:table-column column-width="proportional-column-width(40)" />
											<fo:table-column column-width="proportional-column-width(30)" />
											<fo:table-column column-width="proportional-column-width(30)" />
											<fo:table-body>
												<xsl:for-each select="DatiGenerali/DatiGeneraliDocumento/ScontoMaggiorazione">
													<xsl:if test="Tipo">
														<fo:table-row keep-together.within-page="always">
															<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																<fo:block xsl:use-attribute-sets="table-cell-label-small">
																</fo:block>
																<fo:block>
																	<xsl:choose>
																		<xsl:when test="Tipo = 'SC'">
																			Sconto
																		</xsl:when>
																		<xsl:when test="Tipo = 'MG'">
																			Maggiorazione
																		</xsl:when>
																		<xsl:otherwise>
																			<xsl:value-of select="Tipo" />
																		</xsl:otherwise>
																	</xsl:choose>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																<fo:block xsl:use-attribute-sets="table-cell-label-small">
																	Percentuale:
																</fo:block>
																<fo:block>
																	<xsl:if test="Percentuale">
																		<xsl:value-of select="Percentuale" />
																	</xsl:if>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																<fo:block xsl:use-attribute-sets="table-cell-label-small">
																	Importo:
																</fo:block>
																<fo:block>
																	<xsl:if test="Importo">
																		<xsl:value-of select="Importo" />
																	</xsl:if>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI SCONTO / MAGGIORAZIONE-->
									
									<!--INIZIO FATTURA PRINCIPALE-->
									<xsl:if test="DatiGenerali/FatturaPrincipale/NumeroFatturaPrincipale">
										<fo:table xsl:use-attribute-sets="padding-top-20 table-content font-size-10">
											<fo:table-column column-width="proportional-column-width(50)" />
											<fo:table-column column-width="proportional-column-width(50)" />
											<fo:table-body>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-padding border">
														<fo:block xsl:use-attribute-sets="table-cell-label-small">
															Numero fattura principale:
														</fo:block>
														<fo:block>
															<xsl:if test="DatiGenerali/FatturaPrincipale/NumeroFatturaPrincipale">
																<xsl:value-of select="DatiGenerali/FatturaPrincipale/NumeroFatturaPrincipale" />
															</xsl:if>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-padding border">
														<fo:block xsl:use-attribute-sets="table-cell-label-small">
															Data fattura principale:
														</fo:block>
														<fo:block>
															<xsl:if test="DatiGenerali/FatturaPrincipale/DataFatturaPrincipale">
																<xsl:call-template name="FormatDate">
																	<xsl:with-param name="DateTime" select="DatiGenerali/FatturaPrincipale/DataFatturaPrincipale" />
																</xsl:call-template>
															</xsl:if>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE FATTURA PRINCIPALE-->
									
									<!--INIZIO DATI BENI E SERVIZI-->
									<xsl:if test="DatiBeniServizi/DettaglioLinee">
										<fo:table xsl:use-attribute-sets="padding-top-20 table-content font-size-8">
											<fo:table-column column-width="proportional-column-width(5)" /> <!-- nr. linea -->
											<fo:table-column column-width="proportional-column-width(35)" /> <!-- descrizione / codice articolo / periodo riferimento (inzio/fine) -->
											<fo:table-column column-width="proportional-column-width(10)" /> <!-- quantita' / unita' misura -->
											<fo:table-column column-width="proportional-column-width(15)" /> <!-- prezzo unitario -->
											<fo:table-column column-width="proportional-column-width(15)" /> <!-- sconto/maggiorazione -->
											<fo:table-column column-width="proportional-column-width(5)" /> <!-- aliquota iva -->
											<fo:table-column column-width="proportional-column-width(15)" /> <!-- prezzo totale -->
											
											<fo:table-header>
												<fo:table-row keep-together.within-page="always">
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Nr. Linea</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Descrizione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block text-align="right">Qtà</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block text-align="right">P. Unitario</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block>Sconto / Maggiorazione</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block text-align="right">IVA</fo:block>
													</fo:table-cell>
													<fo:table-cell xsl:use-attribute-sets="cell-header cell-padding border">
														<fo:block text-align="right">P. Totale</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-header>
											<fo:table-body>
												<xsl:for-each select="DatiBeniServizi/DettaglioLinee">
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:value-of select="NumeroLinea" />
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="TipoCessionePrestazione and TipoCessionePrestazione != ''">
																	<fo:block font-size="7pt" space-after="5pt">
																		Tipo cessione/prestazione:
																		
																		<fo:inline font-weight="bold">
																			<xsl:choose>
																				<xsl:when test="TipoCessionePrestazione='SC'">
																					Sconto
																				</xsl:when>
																				<xsl:when test="TipoCessionePrestazione='PR'">
																					Premio
																				</xsl:when>
																				<xsl:when test="TipoCessionePrestazione='AB'">
																					Abbuono
																				</xsl:when>
																				<xsl:when test="TipoCessionePrestazione='AC'">
																					Spesa accessoria
																				</xsl:when>
																				<xsl:otherwise>
																					<xsl:value-of select="TipoCessionePrestazione" />
																				</xsl:otherwise>
																			</xsl:choose>
																		</fo:inline>
																	</fo:block>
																</xsl:if>
																<xsl:if test="Descrizione">
																	<fo:block>
																		<xsl:value-of select="Descrizione" />
																	</fo:block>
																</xsl:if>
																<xsl:if test="DataInizioPeriodo or DataFinePeriodo">
																	<fo:block>
																		<xsl:if test="DataInizioPeriodo">
																			<xsl:call-template name="FormatDate">
																				<xsl:with-param name="DateTime" select="DataInizioPeriodo" />
																			</xsl:call-template>
																		</xsl:if>
																		<xsl:if test="DataInizioPeriodo and DataFinePeriodo">
																			<fo:inline> - </fo:inline>
																		</xsl:if>
																		<xsl:if test="DataFinePeriodo">
																			<xsl:call-template name="FormatDate">
																				<xsl:with-param name="DateTime" select="DataFinePeriodo" />
																			</xsl:call-template>
																		</xsl:if>
																	</fo:block>
																</xsl:if>
																<xsl:if test="CodiceArticolo">
																	<fo:block font-size="7pt" space-before="5pt">
																		<xsl:for-each select="CodiceArticolo">
																			<fo:block>
																				<xsl:value-of select="CodiceTipo" />: <xsl:value-of select="CodiceValore" />
																			</fo:block>
																		</xsl:for-each>
																	</fo:block>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block text-align="right">
																<xsl:if test="Quantita">
																	<xsl:value-of select="Quantita" />
																</xsl:if>
																<xsl:if test="Quantita and UnitaMisura">
																	<xsl:text> </xsl:text>
																</xsl:if>
																<xsl:if test="UnitaMisura">
																	<xsl:value-of select="UnitaMisura" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block text-align="right">
																<xsl:if test="PrezzoUnitario">
																	<xsl:value-of select="PrezzoUnitario" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block>
																<xsl:if test="ScontoMaggiorazione">
																	<xsl:for-each select="ScontoMaggiorazione">
																		<fo:block font-size="7pt">
																			<xsl:if test="Importo">
																				<xsl:if test="Tipo">
																					<xsl:choose>
																						<xsl:when test="Tipo='SC'">
																							Sconto:
																						</xsl:when>
																						<xsl:when test="Tipo='MG'">
																							Maggiorazione:
																						</xsl:when>
																					</xsl:choose>
																				</xsl:if>
																				<xsl:value-of select="Importo" />
																				<xsl:if test="Percentuale">
																					<xsl:text> (</xsl:text><xsl:value-of select="Percentuale" /><xsl:text> %)</xsl:text>
																				</xsl:if>
																			</xsl:if>
																		</fo:block>
																	</xsl:for-each>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block text-align="right">
																<xsl:if test="AliquotaIVA">
																	<xsl:value-of select="AliquotaIVA" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block text-align="right">
																<xsl:if test="PrezzoTotale">
																	<xsl:value-of select="PrezzoTotale" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</xsl:if>
									<!--FINE DATI BENI E SERVIZI-->
									
									<!--INIZIO DATI DI RIEPILOGO ALIQUOTE E NATURE-->
									<xsl:if test="DatiBeniServizi/DatiRiepilogo">
										<fo:block xsl:use-attribute-sets="section-title padding-top-20">Riepilogo</fo:block>
										
										<xsl:for-each select="DatiBeniServizi/DatiRiepilogo">
											<fo:table xsl:use-attribute-sets="table-content border-strong font-size-10">
												<xsl:if test="position() > 0">
													<xsl:attribute name="space-after">0pt</xsl:attribute>
												</xsl:if>
												<xsl:if test="position() != count(DatiBeniServizi/DatiRiepilogo)">
													<xsl:attribute name="space-before">0pt</xsl:attribute>
												</xsl:if>
												<fo:table-column column-width="proportional-column-width(25)" />
												<fo:table-column column-width="proportional-column-width(25)" />
												<fo:table-column column-width="proportional-column-width(25)" />
												<fo:table-column column-width="proportional-column-width(25)" />
												<fo:table-body>
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Aliquota IVA (%):
															</fo:block>
															<fo:block>
																<xsl:if test="AliquotaIVA">
																	<xsl:value-of select="AliquotaIVA" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Natura Operazioni:
															</fo:block>
															<fo:block>
																<xsl:if test="Natura">
																	<xsl:variable name="NAT1">
																		<xsl:value-of select="Natura" />
																	</xsl:variable>
																	<xsl:choose>
																		<xsl:when test="$NAT1='N1'">
																			Escluse ex art.15
																		</xsl:when>
																		<xsl:when test="$NAT1='N2'">
																			Non soggette
																		</xsl:when>
																		<xsl:when test="$NAT1='N3'">
																			Non imponibili
																		</xsl:when>
																		<xsl:when test="$NAT1='N4'">
																			Esenti
																		</xsl:when>
																		<xsl:when test="$NAT1='N5'">
																			Regime del margine
																		</xsl:when>
																		<xsl:when test="$NAT1='N6'">
																			Inversione contabile
																		</xsl:when>
																		<xsl:when test="$NAT1='N7'">
																			IVA assolta in altro stato UE
																		</xsl:when>
																	</xsl:choose>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Spese accessorie:
															</fo:block>
															<fo:block>
																<xsl:if test="SpeseAccessorie">
																	<xsl:value-of select="SpeseAccessorie" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Arrotondamento:
															</fo:block>
															<fo:block>
																<xsl:if test="Arrotondamento">
																	<xsl:value-of select="Arrotondamento" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Totale imponibile/importo:
															</fo:block>
															<fo:block>
																<xsl:if test="ImponibileImporto">
																	<xsl:value-of select="ImponibileImporto" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Totale imposta:
															</fo:block>
															<fo:block>
																<xsl:if test="Imposta">
																	<xsl:value-of select="Imposta" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Esigibilità IVA:
															</fo:block>
															<fo:block>
																<xsl:if test="EsigibilitaIVA">
																	<xsl:variable name="EI">
																		<xsl:value-of select="EsigibilitaIVA" />
																	</xsl:variable>
																	<xsl:choose>
																		<xsl:when test="$EI='I'">
																			Esigibilità immediata
																		</xsl:when>
																		<xsl:when test="$EI='D'">
																			Esigibilità differita
																		</xsl:when>
																		<xsl:when test="$EI='S'">
																			Scissione dei pagamenti
																		</xsl:when>
																	</xsl:choose>
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Riferimento normativo:
															</fo:block>
															<fo:block>
																<xsl:if test="RiferimentoNormativo">
																	<xsl:value-of select="RiferimentoNormativo" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</xsl:for-each>
									</xsl:if>
									<!--FINE DATI DI RIEPILOGO ALIQUOTE E NATURE-->
									
									<!--INIZIO DATI PAGAMENTO-->
									<xsl:if test="DatiPagamento">
										<xsl:for-each select="DatiPagamento">
											<fo:block xsl:use-attribute-sets="section-title padding-top-20">
												Pagamento
												<xsl:choose>
													<xsl:when test="CondizioniPagamento='TP01'">
														(pagamento a rate)
													</xsl:when>
													<xsl:when test="CondizioniPagamento='TP02'">
														(pagamento completo)
													</xsl:when>
													<xsl:when test="CondizioniPagamento='TP03'">
														(anticipo)
													</xsl:when>
												</xsl:choose>
											</fo:block>
											
											<xsl:if test="DettaglioPagamento">
												<xsl:for-each select="DettaglioPagamento">					
													<fo:table xsl:use-attribute-sets="table-content border-strong font-size-10">
														<xsl:if test="position() > 0">
															<xsl:attribute name="space-after">0pt</xsl:attribute>
														</xsl:if>
														<xsl:if test="position() != count(DettaglioPagamento)">
															<xsl:attribute name="space-before">0pt</xsl:attribute>
														</xsl:if>
														<fo:table-column column-width="proportional-column-width(25)" />
														<fo:table-column column-width="proportional-column-width(25)" />
														<fo:table-column column-width="proportional-column-width(25)" />
														<fo:table-column column-width="proportional-column-width(25)" />
														
														<fo:table-body>
															<fo:table-row keep-together.within-page="always">
																<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																	<fo:block xsl:use-attribute-sets="table-cell-label-small">
																		Beneficiario del pagamento:
																	</fo:block>
																	<fo:block>
																		<xsl:if test="Beneficiario">
																			<xsl:value-of select="Beneficiario" />
																		</xsl:if>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																	<fo:block xsl:use-attribute-sets="table-cell-label-small">
																		Modalità:
																	</fo:block>
																	<fo:block>
																		<xsl:variable name="MP">
																			<xsl:value-of select="ModalitaPagamento" />
																		</xsl:variable>
																		<xsl:choose>
																			<xsl:when test="$MP='MP01'">
																				Contanti
																			</xsl:when>
																			<xsl:when test="$MP='MP02'">
																				Assegno
																			</xsl:when>
																			<xsl:when test="$MP='MP03'">
																				Assegno circolare
																			</xsl:when>
																			<xsl:when test="$MP='MP04'">
																				Contanti presso Tesoreria
																			</xsl:when>
																			<xsl:when test="$MP='MP05'">
																				Bonifico
																			</xsl:when>
																			<xsl:when test="$MP='MP06'">
																				Vaglia cambiario
																			</xsl:when>
																			<xsl:when test="$MP='MP07'">
																				Bollettino bancario
																			</xsl:when>
																			<xsl:when test="$MP='MP08'">
																				Carta di pagamento
																			</xsl:when>
																			<xsl:when test="$MP='MP09'">
																				RID
																			</xsl:when>
																			<xsl:when test="$MP='MP10'">
																				RID utenze
																			</xsl:when>
																			<xsl:when test="$MP='MP11'">
																				RID veloce
																			</xsl:when>
																			<xsl:when test="$MP='MP12'">
																				RIBA
																			</xsl:when>
																			<xsl:when test="$MP='MP13'">
																				MAV
																			</xsl:when>
																			<xsl:when test="$MP='MP14'">
																				Quietanza erario
																			</xsl:when>
																			<xsl:when test="$MP='MP15'">
																				Giroconto su conti di contabilità speciale
																			</xsl:when>
																			<xsl:when test="$MP='MP16'">
																				Domiciliazione bancaria
																			</xsl:when>
																			<xsl:when test="$MP='MP17'">
																				Domiciliazione postale
																			</xsl:when>
																			<xsl:when test="$MP='MP18'">
																				Bollettino di c/c postale
																			</xsl:when>
																			<xsl:when test="$MP='MP19'">
																				SEPA Direct Debit
																			</xsl:when>
																			<xsl:when test="$MP='MP20'">
																				SEPA Direct Debit CORE
																			</xsl:when>
																			<xsl:when test="$MP='MP21'">
																				SEPA Direct Debit B2B
																			</xsl:when>
																			<xsl:when test="$MP='MP22'">
																				Trattenuta su somme già riscosse
																			</xsl:when>
																		</xsl:choose>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																	<fo:block xsl:use-attribute-sets="table-cell-label-small">
																		Codice pagamento:
																	</fo:block>
																	<fo:block>
																		<xsl:attribute name="font-size">
																			<xsl:choose>
																				<xsl:when test="CodicePagamento and string-length(CodicePagamento) > 25">
																					<xsl:text>8pt</xsl:text>
																				</xsl:when>
																				<xsl:otherwise>
																					<xsl:text>10pt</xsl:text>
																				</xsl:otherwise>
																			</xsl:choose>
																		</xsl:attribute>
																		<xsl:if test="CodicePagamento">
																			<xsl:value-of select="CodicePagamento" />
																		</xsl:if>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
															<fo:table-row keep-together.within-page="always">
																<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																	<fo:block xsl:use-attribute-sets="table-cell-label-small">
																		Decorrenza termini di pagamento:
																	</fo:block>
																	<fo:block>
																		<xsl:if test="DataRiferimentoTerminiPagamento">
																			<xsl:call-template name="FormatDate">
																				<xsl:with-param name="DateTime" select="DataRiferimentoTerminiPagamento" />
																			</xsl:call-template>
																		</xsl:if>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																	<fo:block xsl:use-attribute-sets="table-cell-label-small">
																		Termini di pagamento (in giorni):
																	</fo:block>
																	<fo:block>
																		<xsl:if test="GiorniTerminiPagamento">
																			<xsl:value-of select="GiorniTerminiPagamento" />
																		</xsl:if>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																	<fo:block xsl:use-attribute-sets="table-cell-label-small">
																		Data scadenza pagamento:
																	</fo:block>
																	<fo:block>
																		<xsl:if test="DataScadenzaPagamento">
																			<xsl:call-template name="FormatDate">
																				<xsl:with-param name="DateTime" select="DataScadenzaPagamento" />
																			</xsl:call-template>
																		</xsl:if>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																	<fo:block xsl:use-attribute-sets="table-cell-label-small">
																		Importo:
																	</fo:block>
																	<fo:block>
																		<xsl:if test="ImportoPagamento">
																			<xsl:value-of select="ImportoPagamento" />
																		</xsl:if>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
															<xsl:if test="CodUfficioPostale or CognomeQuietanzante or NomeQuietanzante or CFQuietanzante">
																<fo:table-row keep-together.within-page="always">
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Codice Ufficio Postale:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="CodUfficioPostale">
																				<xsl:value-of select="CodUfficioPostale" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Quietanzante:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="CognomeQuietanzante">
																				<xsl:value-of select="CognomeQuietanzante" />
																			</xsl:if>
																			<xsl:if test="CognomeQuietanzante and NomeQuietanzante">
																				<xsl:text> </xsl:text>
																			</xsl:if>
																			<xsl:if test="NomeQuietanzante">
																				<xsl:value-of select="NomeQuietanzante" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			CF del quietanzante:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="CFQuietanzante">
																				<xsl:value-of select="CFQuietanzante" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</xsl:if>
															<xsl:if test="IstitutoFinanziario">
																<fo:table-row keep-together.within-page="always">
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="4">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Istituto finanziario:
																		</fo:block>
																		<fo:block>
																			<xsl:value-of select="IstitutoFinanziario" />
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</xsl:if>
															<xsl:if test="IBAN or ABI">
																<fo:table-row keep-together.within-page="always">
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Codice IBAN:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="IBAN">
																				<xsl:value-of select="IBAN" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Codice ABI:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="ABI">
																				<xsl:value-of select="ABI" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</xsl:if>
															<xsl:if test="CAB or BIC">
																<fo:table-row keep-together.within-page="always">
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Codice CAB:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="CAB">
																				<xsl:value-of select="CAB" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Codice BIC:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="BIC">
																				<xsl:value-of select="BIC" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</xsl:if>
															<xsl:if test="ScontoPagamentoAnticipato or DataLimitePagamentoAnticipato">
																<fo:table-row keep-together.within-page="always">
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Sconto per pagamento anticipato:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="ScontoPagamentoAnticipato">
																				<xsl:value-of select="ScontoPagamentoAnticipato" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Data limite per il pagamento anticipato:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="DataLimitePagamentoAnticipato">
																				<xsl:call-template name="FormatDate">
																					<xsl:with-param name="DateTime" select="DataLimitePagamentoAnticipato" />
																				</xsl:call-template>
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</xsl:if>
															<xsl:if test="PenalitaPagamentiRitardati or DataDecorrenzaPenale">
																<fo:table-row keep-together.within-page="always">
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Penale per ritardato pagamento:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="PenalitaPagamentiRitardati">
																				<xsl:value-of select="PenalitaPagamentiRitardati" />
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="2">
																		<fo:block xsl:use-attribute-sets="table-cell-label-small">
																			Data di decorrenza della penale:
																		</fo:block>
																		<fo:block>
																			<xsl:if test="DataDecorrenzaPenale">
																				<xsl:call-template name="FormatDate">
																					<xsl:with-param name="DateTime" select="DataDecorrenzaPenale" />
																				</xsl:call-template>
																			</xsl:if>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</xsl:if>
														</fo:table-body>
													</fo:table>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:if>
									<!--FINE DATI PAGAMENTO-->
									
									<!--INIZIO ALLEGATI-->
									<xsl:if test="Allegati">
										<fo:block xsl:use-attribute-sets="section-title padding-top-20">Allegati</fo:block>
										
										<xsl:for-each select="Allegati">
											<fo:table xsl:use-attribute-sets="table-content border-strong font-size-10">
												<xsl:if test="position() > 0">
													<xsl:attribute name="space-after">0pt</xsl:attribute>
												</xsl:if>
												<xsl:if test="position() != count(Allegati)">
													<xsl:attribute name="space-before">0pt</xsl:attribute>
												</xsl:if>
												<fo:table-column column-width="proportional-column-width(50)" />
												<fo:table-column column-width="proportional-column-width(30)" />
												<fo:table-column column-width="proportional-column-width(20)" />
												<fo:table-body>
													<fo:table-row keep-together.within-page="always">
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Nome dell'allegato:
															</fo:block>
															<fo:block>
																<xsl:if test="NomeAttachment">
																	<xsl:value-of select="NomeAttachment" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Algoritmo di compressione:
															</fo:block>
															<fo:block>
																<xsl:if test="AlgoritmoCompressione">
																	<xsl:value-of select="AlgoritmoCompressione" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell xsl:use-attribute-sets="cell-padding border">
															<fo:block xsl:use-attribute-sets="table-cell-label-small">
																Formato:
															</fo:block>
															<fo:block>
																<xsl:if test="FormatoAttachment">
																	<xsl:value-of select="FormatoAttachment" />
																</xsl:if>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:if test="DescrizioneAttachment">
														<fo:table-row keep-together.within-page="always">
															<fo:table-cell xsl:use-attribute-sets="cell-padding border" number-columns-spanned="3">
																<fo:block xsl:use-attribute-sets="table-cell-label-small">
																	Descrizione allegato:
																</fo:block>
																<fo:block>
																	<xsl:value-of select="DescrizioneAttachment" />
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
												</fo:table-body>
											</fo:table>
										</xsl:for-each>
									</xsl:if>
									<!--FINE ALLEGATI-->
									
								</xsl:for-each>
							</fo:block>
						</xsl:if>
					</xsl:if>
				
				</fo:flow>
				<!-- FINE CORPO DI OGNI PAGINA DELLA FATTURA -->
				
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	
</xsl:stylesheet>