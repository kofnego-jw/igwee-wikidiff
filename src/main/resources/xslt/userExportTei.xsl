<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:uibk="http://www.uibk.ac.at/igwee/ns"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:wiki="http://www.mediawiki.org/xml/export-0.10/"
    xmlns="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs math tei uibk"
    version="2.0">
    
    <xsl:output method="xml" indent="no"/>
    
    <xsl:param name="userID">1187066</xsl:param>
    
    <xsl:variable name="userid" select="$userID"/>    
    
    <xsl:template match="/">
        <TEI>
            <teiHeader>
                <fileDesc>
                    <titleStmt>
                        <title><xsl:call-template name="getTitle"/></title>
                        <author>Exported from WebApp igwee-wikidiff</author>
                    </titleStmt>
                    <publicationStmt>
                        <p>Derived License from WikiPedia: CC 3.0 By Unported</p>
                    </publicationStmt>
                    <sourceDesc>
                        <p>See the original XML File.</p>
                    </sourceDesc>
                </fileDesc>
            </teiHeader>
            <text>
                <body>
                    <div>
                        <head>
                            <xsl:text>Revisions by </xsl:text>
                            <hi rend="bold"><xsl:call-template name="getUsername"/></hi>
                            <xsl:text> (ID: </xsl:text>
                            <xsl:call-template name="getUserId"/>
                            <xsl:text>) in Page </xsl:text>
                            <hi rend="bold"><xsl:call-template name="getTitle"/></hi></head>
                        <div>
                            <head>Statistics</head>
                            <p><xsl:text>There are </xsl:text>
                                <xsl:call-template name="getCountAll"/>
                                <xsl:text> revisions in this file, and </xsl:text> 
                                <xsl:call-template name="getCountUserRevisions"/>
                                <xsl:text> are by </xsl:text> 
                                <xsl:call-template name="getUsername"/>
                                <xsl:text>.</xsl:text></p>
                        </div>
                        <div>
                            <head>Revisions</head>
                            <xsl:call-template name="writeRevisions"/>
                        </div>
                    </div>
                </body>
            </text>
        </TEI>
    </xsl:template>
    
    <xsl:template name="writeRevisions">
        <xsl:for-each select="//wiki:revision[wiki:contributor/wiki:id=$userid] | //wiki:revision[wiki:contributor/wiki:ip=$userid]">
            <div>
                <head>
                    <xsl:call-template name="getTime">
                        <xsl:with-param name="revision" select="."/>
                    </xsl:call-template>
                </head>
                <div type="comment">
                    <head>Comment</head>
                    <xsl:apply-templates select="wiki:comment"/>
                </div>
                <div type="originalText">
                    <head>Revisiontext</head>
                    <xsl:apply-templates select="wiki:text"/>
                </div>
                <div type="diffText">
                    <head>Diff with last revision not by <xsl:call-template name="getUsername"/></head>
                    <xsl:choose>
                        <xsl:when test="(./preceding-sibling::wiki:revision[wiki:contributor/wiki:id!=$userid])[1]">
                            <xsl:variable name="prevRevision" select="(./preceding-sibling::wiki:revision[wiki:contributor/wiki:id!=$userid])[1]"/>
                            <head>Revision by <xsl:value-of select="$prevRevision/wiki:contributor/wiki:username"/> 
                                (<xsl:value-of select="$prevRevision/wiki:contributor/wiki:id"/>) 
                                on <xsl:value-of select="$prevRevision/wiki:timestamp"/>.</head>
                            <div>
                                <xsl:for-each select="uibk:diff($prevRevision/wiki:text, ./wiki:text)">
                                    <xsl:choose>
                                        <xsl:when test="starts-with(., '+')">
                                            <add><xsl:value-of select="substring(., 2)"/></add>
                                        </xsl:when>                                    
                                        <xsl:when test="starts-with(., '-')">
                                            <del><xsl:value-of select="substring(., 2)"/></del>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="substring(., 2)"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:for-each>
                            </div>
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
        <xsl:value-of select="count(//wiki:revision[wiki:contributor/wiki:id=$userid])"/>
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
            <xsl:when test="contains($userid, '.')">
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