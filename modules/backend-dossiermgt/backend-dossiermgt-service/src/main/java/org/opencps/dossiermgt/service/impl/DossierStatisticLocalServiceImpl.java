/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.opencps.dossiermgt.service.impl;

import java.util.LinkedHashMap;

import org.opencps.dossiermgt.constants.DossierStatisticTerm;
import org.opencps.dossiermgt.model.DossierStatistic;
import org.opencps.dossiermgt.service.base.DossierStatisticLocalServiceBaseImpl;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.generic.MultiMatchQuery;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import aQute.bnd.annotation.ProviderType;

/**
 * The implementation of the dossier statistic local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.dossiermgt.service.DossierStatisticLocalService}
 * interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author huymq
 * @see DossierStatisticLocalServiceBaseImpl
 * @see org.opencps.dossiermgt.service.DossierStatisticLocalServiceUtil
 */
@ProviderType
public class DossierStatisticLocalServiceImpl extends DossierStatisticLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link
	 * org.opencps.dossiermgt.service.DossierStatisticLocalServiceUtil} to
	 * access the dossier statistic local service.
	 */
	public Hits searchLucene(LinkedHashMap<String, Object> params, Sort[] sorts, int start, int end,
			SearchContext searchContext) throws ParseException, SearchException {

		String keywords = (String) params.get(Field.KEYWORD_SEARCH);
		String groupId = (String) params.get(Field.GROUP_ID);

		Indexer<DossierStatistic> indexer = IndexerRegistryUtil.nullSafeGetIndexer(DossierStatistic.class);

		searchContext.addFullQueryEntryClassName(DossierStatistic.class.getName());
		searchContext.setEntryClassNames(new String[] { DossierStatistic.class.getName() });
		searchContext.setAttribute("paginationType", "regular");
		searchContext.setLike(true);
		searchContext.setStart(start);
		searchContext.setEnd(end);
		searchContext.setAndSearch(true);
		searchContext.setSorts(sorts);

		BooleanQuery booleanQuery = null;

		if (Validator.isNotNull(keywords)) {
			booleanQuery = BooleanQueryFactoryUtil.create(searchContext);
		} else {
			booleanQuery = indexer.getFullQuery(searchContext);
		}
		
		if (Validator.isNotNull(groupId)) {
			MultiMatchQuery query = new MultiMatchQuery(groupId);

			query.addFields(Field.GROUP_ID);

			booleanQuery.add(query, BooleanClauseOccur.MUST);
		}
		
		// Extra fields
		String year = GetterUtil.getString(params.get(DossierStatisticTerm.YEAR));
		String month = GetterUtil.getString(params.get(DossierStatisticTerm.MONTH));
		String domainCd = GetterUtil.getString(params.get(DossierStatisticTerm.DOMAIN_CODE));
		String agencyCd = GetterUtil.getString(params.get(DossierStatisticTerm.GOV_AGENCY_CODE));
		String level = GetterUtil.getString(params.get(DossierStatisticTerm.ADMINISTRATION_LEVEL));
		if (Validator.isNotNull(year)) {
			MultiMatchQuery query = new MultiMatchQuery(year);

			query.addFields(DossierStatisticTerm.YEAR);

			booleanQuery.add(query, BooleanClauseOccur.MUST);
		}
		
		if (Validator.isNotNull(month)) {
			MultiMatchQuery query = new MultiMatchQuery(month);

			query.addFields(DossierStatisticTerm.MONTH);

			booleanQuery.add(query, BooleanClauseOccur.MUST);
		}
		
		if (Validator.isNotNull(domainCd)) {
			MultiMatchQuery query = new MultiMatchQuery(domainCd);

			query.addFields(DossierStatisticTerm.DOMAIN_CODE);

			booleanQuery.add(query, BooleanClauseOccur.MUST);
		}
		if (Validator.isNotNull(agencyCd)) {
			MultiMatchQuery query = new MultiMatchQuery(agencyCd);

			query.addFields(DossierStatisticTerm.GOV_AGENCY_CODE);

			booleanQuery.add(query, BooleanClauseOccur.MUST);
		}
		if (Validator.isNotNull(level)) {
			MultiMatchQuery query = new MultiMatchQuery(level);

			query.addFields(DossierStatisticTerm.ADMINISTRATION_LEVEL);

			booleanQuery.add(query, BooleanClauseOccur.MUST);
		}
		booleanQuery.addRequiredTerm(Field.ENTRY_CLASS_NAME, DossierStatistic.class.getName());
		return IndexSearcherHelperUtil.search(searchContext, booleanQuery);
	}
}