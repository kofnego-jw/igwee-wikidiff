<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:uibk="http://www.uibk.ac.at/igwee/ns"
    xmlns:wiki="http://www.mediawiki.org/xml/export-0.10/"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xs math html uibk wiki"
    version="2.0">
    
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
    
    <xsl:param name="userID">1187066</xsl:param>
    
    <xsl:variable name="userid" select="$userID"/>
    
    <xsl:template match="/">
        <div class="row">
            <h3><a name="userStart">Revisions</a> by <b><xsl:call-template name="getUsername"/></b>
                (ID: <xsl:call-template name="getUserId"/>) in Page 
                <b><xsl:call-template name="getTitle"/></b></h3>
            <hr/>
            <h4>Statistics</h4>
            <p>There are 
                <xsl:call-template name="getCountAll"/>
                revisions in this file, and  
                <xsl:call-template name="getCountUserRevisions"/>
                is/are by  
                <xsl:call-template name="getUsername"/>.</p>
            <hr/>
            <h4>Revisions</h4>
            <xsl:call-template name="writeRevisions"/>
        </div>
    </xsl:template>
    
    <xsl:template name="writeRevisions">
        <xsl:for-each select="//wiki:revision[wiki:contributor/wiki:id=$userid] | //wiki:revision[wiki:contributor/wiki:ip=$userid] ">
            <xsl:sort select="wiki:timestamp"/>
            <div>
                <h5>Revision
                    <xsl:value-of select="./wiki:id"/> on 
                    <xsl:call-template name="getTime">
                    <xsl:with-param name="revision" select="."/>
                </xsl:call-template></h5>
                <div class="commentArea">
                    <xsl:apply-templates select="wiki:comment"/>
                </div>
                <div class="original">
                    <p><b>Revisiontext</b></p>
                    <xsl:choose>
                        <xsl:when test="wiki:text/@deleted">
                            <p class="originalText">[[TEXT DELETED]]</p>
                        </xsl:when>
                        <xsl:otherwise>
                            <pre class="sourceCode text-area originalText">
                                <xsl:apply-templates select="wiki:text"/>
                            </pre>
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
                <div class="diff">
                    <p><b>Diff with last revision not by <xsl:call-template name="getUsername"/></b></p>
                    <xsl:choose>
                        <xsl:when test="./preceding-sibling::wiki:revision[wiki:contributor/wiki:id!=$userid][1]">
                            <xsl:variable name="prevRevision" select="./preceding-sibling::wiki:revision[wiki:contributor/wiki:id!=$userid][1]"/>
                            <p>Revision <xsl:value-of select="$prevRevision/wiki:id"/> 
                                by <xsl:value-of select="$prevRevision/wiki:contributor/wiki:username"/> 
                                (<xsl:value-of select="$prevRevision/wiki:contributor/wiki:id"/>) 
                                on <xsl:value-of select="$prevRevision/wiki:timestamp"/>.</p>
                            <pre class="text-area sourceCode diffText">
                                <xsl:for-each select="uibk:diff($prevRevision/wiki:text, ./wiki:text)">
                                    <xsl:choose>
                                        <xsl:when test="starts-with(., '+')">
                                            <ins><xsl:value-of select="substring(., 2)"/></ins>
                                        </xsl:when>                                    
                                        <xsl:when test="starts-with(., '-')">
                                            <del><xsl:value-of select="substring(., 2)"/></del>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="substring(., 2)"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:for-each>
                            </pre>
                        </xsl:when>
                        <xsl:otherwise>
                            <p>No previous revision available.</p>
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>
    
    
    <xsl:template match="wiki:comment">
        <p><xsl:apply-templates /></p>
    </xsl:template>
    
    <xsl:template name="getTime">
        <xsl:param name="revision"/>
        <xsl:value-of select="$revision/wiki:timestamp"/>
    </xsl:template>
    
    <xsl:template name="getCountAll">
        <xsl:value-of select="count(//wiki:revision)"/>
    </xsl:template>
    
    <xsl:template name="getCountUserRevisions">
        <xsl:value-of select="count(//wiki:revision[wiki:contributor/wiki:id=$userid] | //wiki:revision[wiki:contributor/wiki:ip=$userid])"/>
    </xsl:template>
    
    <xsl:template name="getTitle">
        <xsl:value-of select="normalize-space(string(//wiki:page/wiki:title))"/>
    </xsl:template>
    
    <xsl:template name="getUserId">
        <xsl:value-of select="$userid"/>
    </xsl:template>
    
    <xsl:template name="getUsername">
        <xsl:choose>
            <xsl:when test="//wiki:contributor[wiki:id=$userid]">
                <xsl:value-of select="(//wiki:contributor[wiki:id=$userid]/wiki:username)[1]"/>
            </xsl:when>
            <xsl:when test="//wiki:contributor[wiki:ip=$userid]">
                <xsl:text>[ip:</xsl:text>
                <xsl:value-of select="$userid"/>
                <xsl:text>]</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>[[Not Found]]</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
</xsl:stylesheet>