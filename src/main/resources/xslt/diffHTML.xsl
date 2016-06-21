<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:wiki="http://www.mediawiki.org/xml/export-0.10/"
    xmlns:uibk="http://www.uibk.ac.at/igwee/ns"
    exclude-result-prefixes="xs math wiki uibk"
    version="2.0">
    
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    
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
        <div class="row">
            <xsl:choose>
                <xsl:when test="$revId1='' or $revId2=''">
                    <div class="col-lg-8 col-lg-offset-2">
                        <p class="bg-danger">Did not get the revision IDs, please pass the ids to the stylesheet.</p>
                    </div>
                </xsl:when>
                <xsl:when test="not(//wiki:revision[wiki:id=$revId1])">
                    <div class="col-lg-8 col-lg-offset-2">
                        <p class="bg-danger">Cannot find the revision with id {$revId1}.</p>
                    </div>
                </xsl:when>
                <xsl:when test="not(//wiki:revision[wiki:id=$revId2])">
                    <div class="col-lg-8 col-lg-offset-2">
                        <p class="bg-danger">Cannot find the revision with id {$revId2}.</p>
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="compare"/>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>
    
    <xsl:template name="compare">
        <xsl:variable name="rev1" select="//wiki:revision[wiki:id=$revId1]"/>
        <xsl:variable name="rev2" select="//wiki:revision[wiki:id=$revId2]"/>
        <div class="col-lg-12">
            <div class="row">
                <div class="col-lg-6">
                    <table class="table">
                        <tbody>
                            <tr>
                                <td colspan="2">Revision <xsl:value-of select="$rev1/wiki:id"/></td>
                            </tr>
                            <tr>
                                <td>Timestamp</td>
                                <td><xsl:value-of select="$rev1/wiki:timestamp"/></td>
                            </tr>
                            <tr>
                                <td>Contributor</td>
                                <td><xsl:value-of select="$rev1/wiki:contributor/wiki:username"/> (<xsl:value-of select="$rev1/wiki:contributor/wiki:id"/>)</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="col-lg-6">
                    <table class="table">
                        <tbody>
                            <tr>
                                <td colspan="2">Revision <xsl:value-of select="$rev2/wiki:id"/></td>
                            </tr>
                            <tr>
                                <td>Timestamp</td>
                                <td><xsl:value-of select="$rev2/wiki:timestamp"/></td>
                            </tr>
                            <tr>
                                <td>Contributor</td>
                                <td><xsl:value-of select="$rev2/wiki:contributor/wiki:username"/> (<xsl:value-of select="$rev2/wiki:contributor/wiki:id"/>)</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-6 pre-scrollable">
                    <pre>
                        <xsl:value-of select="$rev1//wiki:text"/>
                    </pre>
                </div>
                <div class="col-lg-6 pre-scrollable">
                    <pre>
                        <xsl:for-each select="uibk:diff($rev1/wiki:text,$rev2/wiki:text)">
                            <xsl:choose>
                                <xsl:when test="starts-with(.,'+')"><ins><xsl:value-of select="substring(.,2)"/></ins></xsl:when>
                                <xsl:when test="starts-with(.,'-')"><del><xsl:value-of select="substring(.,2)"/></del></xsl:when>
                                <xsl:otherwise><xsl:value-of select="substring(.,2)"/></xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </pre>
                </div>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>