<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:wiki="http://www.mediawiki.org/xml/export-0.10/"
    xmlns:uibk="http://www.uibk.ac.at/igwee/ns"
    exclude-result-prefixes="xs math wiki uibk"
    version="2.0">
    
    <xsl:output method="text"/>
    
    <xsl:param name="revisionID1"></xsl:param>
    <xsl:param name="revisionID2"></xsl:param>
    <xsl:variable name="revId1">
        <xsl:choose>
            <xsl:when test="$revisionID1!=''">
                <xsl:value-of select="$revisionID1"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="(//wiki:revision)[1]/wiki:id"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="revId2">
        <xsl:choose>
            <xsl:when test="$revisionID2!=''">
                <xsl:value-of select="$revisionID2"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="//wiki:revision[wiki:id=$revId1]/following-sibling::wiki:revision">
                        <xsl:value-of select="//wiki:revision[wiki:id=$revId1]/following-sibling::wiki:revision[1]/wiki:id"/>
                    </xsl:when>
                    <xsl:when test="//wiki:revision[wiki:id=$revId1]/preceding-sibling::wiki:revision">
                        <xsl:value-of select="//wiki:revision[wiki:id=$revId1]/preceding-sibling::wiki:revision[1]/wiki:id"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$revId1"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    
    
    <xsl:template match="/">
        <xsl:text>{</xsl:text>
        <xsl:text>"text1":"</xsl:text>
        <xsl:choose>
            <xsl:when test="//wiki:revision[wiki:id=$revId1]">
                <xsl:choose>
                    <xsl:when test="//wiki:revision[wiki:id=$revId1]/wiki:text/@deleted">
                        <xsl:text>[[TEXT DELETED]]</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="//wiki:revision[wiki:id=$revId1]/wiki:text/text()"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>    
            <xsl:otherwise>
                <xsl:text>Cannot find Revision with ID </xsl:text>
                <xsl:value-of select="$revId1"/>
                <xsl:text>.</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>","text2":"</xsl:text>
        <xsl:choose>
            <xsl:when test="//wiki:revision[wiki:id=$revId2]">
                <xsl:choose>
                    <xsl:when test="//wiki:revision[wiki:id=$revId2]/wiki:text/@deleted">
                        <xsl:text>[[TEXT DELETED]]</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="//wiki:revision[wiki:id=$revId2]/wiki:text/text()"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>Cannot find Revision with ID </xsl:text>
                <xsl:value-of select="$revId2"/>
                <xsl:text>.</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>","diff":"</xsl:text>
        <xsl:for-each select="uibk:diff(//wiki:revision[wiki:id=$revId1]/wiki:text,//wiki:revision[wiki:id=$revId2]/wiki:text)">
            <xsl:choose>
                <xsl:when test="starts-with(.,'+')">
                    <xsl:text>&lt;uibk:ins&gt;</xsl:text>
                    <xsl:call-template name="escape-string">
                        <xsl:with-param name="s" select="substring(.,2)"/>
                    </xsl:call-template>
                    <xsl:text>&lt;/uibk:ins&gt;</xsl:text>
                </xsl:when>
                <xsl:when test="starts-with(.,'-')">
                    <xsl:text>&lt;uibk:del&gt;</xsl:text>
                    <xsl:call-template name="escape-string">
                        <xsl:with-param name="s" select="substring(.,2)"/>
                    </xsl:call-template>
                    <xsl:text>&lt;/uibk:del&gt;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="escape-string">
                        <xsl:with-param name="s" select="substring(.,2)"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:text>"}</xsl:text>
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