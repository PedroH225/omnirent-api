package br.com.omnirent.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.omnirent.item.domain.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

}
