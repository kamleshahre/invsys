package org.sab.invsys.service.payments;

import java.util.List;

import org.apache.log4j.Logger;
import org.sab.invsys.common.util.mapper.payment.UserPaymentMapper;
import org.sab.invsys.persistence.model.payments.UserPayment;
import org.sab.invsys.persistence.model.user.User;
import org.sab.invsys.persistence.repo.payments.UserPaymentRepository;
import org.sab.invsys.persistence.repo.user.UserRepository;
import org.sab.invsys.web.model.payments.UserPaymentUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPaymentService {

	@Autowired
	private UserPaymentRepository repository;

	@Autowired
	private UserRepository userRepository;

	private Logger logger = Logger.getLogger(UserPaymentService.class);

	private UserPaymentMapper mapper = new UserPaymentMapper();

	@Transactional
	public UserPaymentUI create(UserPaymentUI uiBean) {

		UserPayment newUserPayment = mapper.toPersistenceBean(uiBean);

		if(uiBean.getUserName() == null)
		{
			return null;
		}
		
		User user = userRepository.findByUsername(uiBean.getUserName());
		newUserPayment.setUser(user);

		UserPayment saved = repository.save(newUserPayment);
		logger.debug("Added Payment : " + saved);

		return mapper.toUIBean(saved);
	}

	public UserPaymentUI find(long paymentId) {
		return mapper.toUIBean(repository.findById(paymentId));
	}

	public List<UserPaymentUI> findAll() {
		return mapper.toUIBean(repository.findAll());
	}

	/*
	 * public Page<UserPaymentUI> findAll(Pageable pageable,
	 * List<FilterRequest> filters) {
	 * 
	 * Predicate predicate = toPredicate(filters); return
	 * mapper.toUIBean(repository.findAll(predicate, pageable), pageable); }
	 */
	public List<UserPaymentUI> findByUser(String userName) {
		User user = new User();
		user.setUsername(userName);
		return mapper.toUIBean(repository.findByUser(user));
	}

	public UserPaymentUI update(UserPaymentUI uiBean) {
		UserPayment existing = repository.findById(uiBean.getId());

		if (existing == null) {
			return null;
		}

		existing.setAmount(uiBean.getAmount());
		existing.setNotes(uiBean.getNotes());

		UserPayment saved = null;

		try {
			saved = repository.save(existing);
		} catch (Exception e) {
			logger.error(e);
		}

		return mapper.toUIBean(saved);
	}

	public Boolean delete(UserPaymentUI uiBean) {
		UserPayment existing = repository.findById(uiBean.getId());

		if (existing == null) {
			return false;
		}

		repository.delete(existing);
		return true;
	}
	/*
	 * private Predicate toPredicate(final List<FilterRequest> filters) {
	 * logger.info("Entering predicates :: " + filters);
	 * 
	 * QUserPayment UserPayment = QUserPayment.UserPayment; BooleanExpression
	 * result = null;
	 * 
	 * try { for (FilterRequest filter : filters) {
	 * 
	 * COLUMNS column = COLUMNS.valueOf(filter.getProperty() .toUpperCase());
	 * BooleanExpression expression = null;
	 * 
	 * switch (column) { case UserPaymentNAME: if (filter.getValue() != null &&
	 * !"".equals(filter.getValue())) { expression =
	 * UserPayment.UserPaymentName.like("%" + filter.getValue() + "%"); } break;
	 * case DESCRIPTION: if (filter.getValue() != null &&
	 * !"".equals(filter.getValue())) { expression =
	 * UserPayment.description.like("%" + filter.getValue() + "%"); } break;
	 * case UserPaymentGROUP: if (filter.getValue() != null &&
	 * !"".equals(filter.getValue())) { expression =
	 * UserPayment.group.groupName.like("%" + filter.getValue() + "%"); } break;
	 * } if (expression != null) { if (result != null) { result =
	 * result.and(expression); } else { result = expression; } } } } catch
	 * (Exception ex) { logger.error(ex); } logger.info("Result Predicate :: " +
	 * (result != null ? result.toString() : ""));
	 * 
	 * logger.info("Exiting predicates"); return result; }
	 */
}
