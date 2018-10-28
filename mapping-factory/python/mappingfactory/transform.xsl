<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:output method="xml" indent="yes" />

	<xsl:key name="primitive" match="package/primitiveType" use="vodml-id" />


	<xsl:template match="objectType">
		<xsl:text disable-output-escaping="yes">&lt;OBJECT </xsl:text>
		dmrole="
		<xsl:value-of select="vodml-id" />
		<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
		<xsl:apply-templates select="attribute" />
		<xsl:apply-templates select="objectType" />
		<xsl:text disable-output-escaping="yes">&#xa;&lt;/OBJECT&gt; &#13;</xsl:text>

	</xsl:template>

	<xsl:template match="attribute">
		ATTRIBUTE: ID:
		<xsl:value-of select="vodml-id" />
		NAME:
		<xsl:value-of select="name" />
		TYPE :
		<xsl:value-of select="substring-after(datatype/vodml-ref, ':')" />
		==================
		<xsl:choose>
			<xsl:when
				test="key('primitive', substring-after(datatype/vodml-ref, ':'))">

				<xsl:text disable-output-escaping="yes">&#13;&lt;FILLER/&gt;&#13;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				CLASSE REF
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="package">
		<xsl:text disable-output-escaping="yes">&lt;PACKAGE dmrole="</xsl:text>
		<!-- xsl:value-of select="vodml-id" / -->
		<xsl:text disable-output-escaping="yes">"&gt;&#13;</xsl:text>
		<xsl:apply-templates select="objectType" />

		<xsl:text disable-output-escaping="yes">&#xa;&lt;/PACKAGE&gt; &#13;</xsl:text>
		<!-- <xsl:copy> <xsl:copy-of select="@*" /> <xsl:apply-templates /> </xsl:copy> -->
	</xsl:template>



</xsl:stylesheet>