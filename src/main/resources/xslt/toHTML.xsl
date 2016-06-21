<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:wiki="http://www.mediawiki.org/xml/export-0.10/"
    xmlns:uibk="http://www.uibk.ac.at/igwee/ns"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xs wiki uibk"
    version="2.0">
    
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    
    <xsl:param name="revisionID">100229180</xsl:param>
    
    <xsl:template match="/">
        <div class="revision row">
            <xsl:choose>
                <xsl:when test="//wiki:revision[wiki:id=$revisionID]">
                    <xsl:apply-templates select="//wiki:revision[wiki:id=$revisionID]"/>
                </xsl:when>
                <xsl:otherwise>
                    <div class="col-lg-8 col-lg-offset-2">
                        <p class="text-danger">
                            <xsl:text>Cannot find a revision with ID </xsl:text>
                            <strong>
                                <xsl:value-of select="$revisionID"/>
                            </strong>
                            <xsl:text>.</xsl:text>
                        </p>
                    </div>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>
    
    <xsl:template match="wiki:revision">
        <div class="col-lg-12">
            <div class="row">
                <div class="cpl-lg-8 col-lg-offset-2">
                    <h3>Metadata</h3>
                    <table class="table">
                        <tbody>
                            <tr>
                                <td>ID</td>
                                <td>
                                    <xsl:value-of select="wiki:id"/>
                                </td>
                            </tr>
                            <tr>
                                <td>Timestamp</td>
                                <td>
                                    <xsl:value-of select="wiki:timestamp"/>
                                </td>
                            </tr>
                            <tr>
                                <td>Contributor</td>
                                <td>
                                    <xsl:choose>
                                        <xsl:when test="wiki:contributor/wiki:username">
                                            <xsl:value-of select="wiki:contributor/wiki:username"/>
                                            <xsl:text>(ID </xsl:text>
                                            <xsl:value-of select="wiki:contributor/wiki:id"/>
                                            <xsl:text>)</xsl:text>
                                        </xsl:when>
                                        <xsl:when test="wiki:contributor/wiki:ip">
                                            <xsl:text>[IP: </xsl:text>
                                            <xsl:value-of select="wiki:contributor/wiki:ip"/>
                                            <xsl:text>]</xsl:text>
                                        </xsl:when>
                                    </xsl:choose>
                                </td>
                            </tr>
                            <tr>
                                <td>Comment</td>
                                <td>
                                    <xsl:choose>
                                        <xsl:when test="wiki:comment">
                                            <xsl:value-of select="wiki:comment"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>--</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <xsl:choose>
                        <xsl:when test="wiki:text/@deleted">
                            [[TEXT DELETED]]
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="uibk:wiki(wiki:text, '', '')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
        </div>
        
    </xsl:template>
    
</xsl:stylesheet>