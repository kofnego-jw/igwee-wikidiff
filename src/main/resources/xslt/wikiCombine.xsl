<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:wiki="http://www.mediawiki.org/xml/export-0.10/"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd" 
    xml:lang="en"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:param name="mergeUrl"></xsl:param>
    
    <xsl:output method="xml" indent="no"/>
    
    <xsl:template match="/">
        <xsl:variable name="integrated">
            <xsl:apply-templates mode="integrate"/>
        </xsl:variable>
        <xsl:apply-templates select="$integrated" mode="sort"/>
    </xsl:template>
    
    
    <xsl:template match="wiki:*|@*|text()" mode="integrate">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="integrate"/>
            <xsl:apply-templates mode="integrate"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="wiki:page" mode="integrate">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="integrate"/>
            <xsl:apply-templates select="wiki:*[name()!='revision']" mode="integrate"/>
            <xsl:if test="doc-available($mergeUrl) and doc($mergeUrl)//wiki:revision">
                <xsl:apply-templates select="doc($mergeUrl)//wiki:revision" mode="integrate"/>
            </xsl:if>
            <xsl:apply-templates select="wiki:revision" mode="integrate"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="wiki:*|@*|text()" mode="sort">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="sort"/>
            <xsl:apply-templates mode="sort"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="wiki:page" mode="sort">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="wiki:*[name()!='revision']"/>
            <xsl:for-each select="wiki:revision">
                <xsl:sort select="wiki:timestamp"/>
                <xsl:apply-templates select="." mode="sort"/>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>