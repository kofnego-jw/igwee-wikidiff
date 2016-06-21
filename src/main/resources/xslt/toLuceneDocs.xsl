<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:wiki="http://www.mediawiki.org/xml/export-0.10/"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output method="text"/>
    
    <xsl:template match="/">
        <xsl:text>{"luceneDocs":[</xsl:text>
        <xsl:for-each select="//wiki:revision">
            <xsl:text>{"docId":"</xsl:text>
            <xsl:apply-templates select="wiki:id"/>
            <xsl:text>","contents":[</xsl:text>
            <xsl:text>{"fieldname":"id","content":"</xsl:text>
            <xsl:apply-templates select="wiki:id"/>
            <xsl:text>"},{"fieldname":"timestamp","content":"</xsl:text>
            <xsl:apply-templates select="wiki:timestamp"/>
            <xsl:text>"},{"fieldname":"username","content":"</xsl:text>
            <xsl:choose>
                <xsl:when test="wiki:contributor/wiki:username">
                    <xsl:apply-templates select="wiki:contributor/wiki:username"/>
                </xsl:when>
                <xsl:when test="wiki:contributor/wiki:ip">
                    <xsl:apply-templates select="wiki:contributor/wiki:ip"/>
                </xsl:when>
            </xsl:choose>            
            <xsl:text>"},{"fieldname":"userid","content":"</xsl:text>
            <xsl:choose>
                <xsl:when test="wiki:contributor/wiki:id">
                    <xsl:apply-templates select="wiki:contributor/wiki:id"/>
                </xsl:when>
                <xsl:when test="wiki:contributor/wiki:ip">
                    <xsl:apply-templates select="wiki:contributor/wiki:ip"/>
                </xsl:when>
            </xsl:choose>            
            <xsl:text>"},{"fieldname":"comment","content":"</xsl:text>
            <xsl:apply-templates select="wiki:comment"/>
            <xsl:text>"},{"fieldname":"revision","content":"</xsl:text>
            <xsl:apply-templates select="wiki:text"/>
            <xsl:text>"}]}</xsl:text>
            <xsl:if test="position()!=last()">
                <xsl:text>,</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>]}</xsl:text>
    </xsl:template>
    
    <xsl:template match="text()">
        <xsl:call-template name="escape-string">
            <xsl:with-param name="s" select="."/>
        </xsl:call-template>
    </xsl:template>
    
    <!-- Main template for escaping strings; used by above template and for object-properties 
       Responsibilities: placed quotes around string, and chain up to next filter, escape-bs-string -->
    <xsl:template name="escape-string">
        <xsl:param name="s"/>
        <xsl:call-template name="escape-bs-string">
            <xsl:with-param name="s" select="$s"/>
        </xsl:call-template>
    </xsl:template>
    
    <!-- Escape the backslash (\) before everything else. -->
    <xsl:template name="escape-bs-string">
        <xsl:param name="s"/>
        <xsl:choose>
            <xsl:when test="contains($s,'\')">
                <xsl:call-template name="escape-quot-string">
                    <xsl:with-param name="s" select="concat(substring-before($s,'\'),'\\')"/>
                </xsl:call-template>
                <xsl:call-template name="escape-bs-string">
                    <xsl:with-param name="s" select="substring-after($s,'\')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="escape-quot-string">
                    <xsl:with-param name="s" select="$s"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Escape the double quote ("). -->
    <xsl:template name="escape-quot-string">
        <xsl:param name="s"/>
        <xsl:choose>
            <xsl:when test="contains($s,'&quot;')">
                <xsl:call-template name="encode-string">
                    <xsl:with-param name="s" select="concat(substring-before($s,'&quot;'),'\&quot;')"/>
                </xsl:call-template>
                <xsl:call-template name="escape-quot-string">
                    <xsl:with-param name="s" select="substring-after($s,'&quot;')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="encode-string">
                    <xsl:with-param name="s" select="$s"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Replace tab, line feed and/or carriage return by its matching escape code. Can't escape backslash
       or double quote here, because they don't replace characters (&#x0; becomes \t), but they prefix 
       characters (\ becomes \\). Besides, backslash should be seperate anyway, because it should be 
       processed first. This function can't do that. -->
    <xsl:template name="encode-string">
        <xsl:param name="s"/>
        <xsl:choose>
            <!-- tab -->
            <xsl:when test="contains($s,'&#x9;')">
                <xsl:call-template name="encode-string">
                    <xsl:with-param name="s" select="concat(substring-before($s,'&#x9;'),'\t',substring-after($s,'&#x9;'))"/>
                </xsl:call-template>
            </xsl:when>
            <!-- line feed -->
            <xsl:when test="contains($s,'&#xA;')">
                <xsl:call-template name="encode-string">
                    <xsl:with-param name="s" select="concat(substring-before($s,'&#xA;'),'\n',substring-after($s,'&#xA;'))"/>
                </xsl:call-template>
            </xsl:when>
            <!-- carriage return -->
            <xsl:when test="contains($s,'&#xD;')">
                <xsl:call-template name="encode-string">
                    <xsl:with-param name="s" select="concat(substring-before($s,'&#xD;'),'\r',substring-after($s,'&#xD;'))"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise><xsl:value-of select="$s"/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>